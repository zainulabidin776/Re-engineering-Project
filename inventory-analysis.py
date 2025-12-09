#!/usr/bin/env python3
"""
generate_inventory_analysis.py

Inventory analysis generator for a legacy Java POS project.

Usage:
    python generate_inventory_analysis.py

Output:
    inventory-analysis.md (markdown file in repository root)

Notes:
 - Designed to be run from the repository root (e.g. inside VSCode workspace).
 - Uses heuristics (file names, imports, text patterns) to build the inventory.
 - Does not modify project files — only reads them.
"""

import os
import re
import shutil
from pathlib import Path
from collections import defaultdict

ROOT = Path.cwd()
OUTPUT_MD = ROOT / "inventory-analysis.md"

# Patterns & heuristics
JAVA_EXT = ".java"
TXT_EXT = ".txt"
JAR_EXT = ".jar"
NETBEANS_MARKERS = ["nbproject", "build.xml", "manifest.mf"]
ANT_MARKER = "build.xml"
SENSITIVE_PATTERNS = [
    re.compile(r'password\s*=\s*"(.*?)"', re.I),
    re.compile(r'pass(word)?\s*:\s*"(.*?)"', re.I),
    re.compile(r'credentials?', re.I),
]
SWING_IMPORTS = re.compile(r'import\s+javax\.swing|extends\s+JFrame|javax\.awt', re.I)
FILE_IO_PATTERNS = re.compile(r'new\s+File\(|FileReader|BufferedReader|FileWriter|FileInputStream|new\s+FileInputStream', re.I)
TXT_PATH_PATTERN = re.compile(r'["\']([^"\']+\.txt)["\']', re.I)
CLASS_DECL = re.compile(r'(public\s+)?(class|interface|enum)\s+([A-Za-z0-9_]+)')

# Utility functions
def find_files(patterns, root=ROOT):
    results = []
    for p in root.rglob('*'):
        if any(p.match(pattern) for pattern in patterns):
            results.append(p)
    return results

def simple_read(path, max_bytes=200_000):
    try:
        with open(path, 'r', encoding='utf-8', errors='ignore') as f:
            return f.read(max_bytes)
    except Exception:
        return ''

def detect_java_classes(java_files):
    classes = {}
    for f in java_files:
        text = simple_read(f)
        m = CLASS_DECL.search(text)
        if m:
            classes[f.relative_to(ROOT).as_posix()] = m.group(3)
        else:
            classes[f.relative_to(ROOT).as_posix()] = None
    return classes

def detect_patterns(file_paths, regex):
    hits = defaultdict(list)
    for f in file_paths:
        text = simple_read(f)
        if regex.search(text):
            hits[f.relative_to(ROOT).as_posix()].append(True)
    return hits

def search_txt_references(java_files):
    refs = set()
    for f in java_files:
        text = simple_read(f)
        for m in TXT_PATH_PATTERN.finditer(text):
            refs.add(m.group(1))
    return sorted(refs)

def classify_assets(all_files, java_files, txt_files, jar_files, tests, build_files, netbeans_folder):
    classification = {}

    # Detect core domain classes by name
    core_names = {"Employee", "Item", "Inventory", "POSSystem", "Sale", "Register"}
    for jf in java_files:
        name = jf.stem
        rel = jf.relative_to(ROOT).as_posix()
        if name in core_names:
            classification[rel] = ("Core domain class", "Reuse with refactor")
        elif SWING_IMPORTS.search(simple_read(jf)):
            classification[rel] = ("UI Swing form / UI class", "Replace (web UI)")
        elif FILE_IO_PATTERNS.search(simple_read(jf)):
            classification[rel] = ("Code with direct file IO", "Refactor -> add repository abstraction")
        else:
            classification[rel] = ("Java source", "Keep → Refactor (evaluate)")

    # Flat files
    for t in txt_files:
        classification[t.relative_to(ROOT).as_posix()] = ("Flat-file DB", "Replace → migrate to RDBMS")

    # Build files
    for b in build_files:
        classification[b.relative_to(ROOT).as_posix()] = ("Build config", "Keep (reference); modernize to Gradle/Maven")

    # Jars / binaries
    for j in jar_files:
        classification[j.relative_to(ROOT).as_posix()] = ("Legacy executable / binary", "Archive (use for behavior reference)")

    # Tests
    for t in tests:
        classification[t.relative_to(ROOT).as_posix()] = ("Test file", "Keep → Expand (add characterization tests)")

    # NetBeans folder
    if netbeans_folder:
        classification[str(netbeans_folder.relative_to(ROOT).as_posix())] = ("IDE metadata (NetBeans)", "Reference / Keep for provenance")

    return classification

def generate_markdown(report):
    lines = []
    lines.append("# Inventory Analysis – Legacy POS Baseline\n")
    lines.append("## 1. Scope and Objectives")
    lines.append("- Establish authoritative list of inherited artefacts before restructuring.")
    lines.append("- Classify each item as **Keep/Refactor**, **Replace**, or **Archive** to guide reengineering phases in the project brief.")
    lines.append("- Capture immediate risks, tooling assumptions, and gaps to unblock reverse-engineering and documentation updates.\n")

    lines.append("## 2. Repository Snapshot (Master Branch)\n")
    lines.append("| Category | Asset(s) | Description | Status |")
    lines.append("| --- | --- | --- | --- |")

    # Provide summarized rows for main categories based on findings
    # Source code
    src_count = len(report["java_files"])
    source_row_desc = f"{src_count} Java files (src/**) — scanned for classes, UI, file-IO."
    src_status = "Keep → Refactor"
    lines.append(f"| Source code | `*.java` ({src_count} files) | {source_row_desc} | {src_status} |")

    # Tests
    test_count = len(report["tests"])
    test_row_desc = f"{test_count} test files found (likely JUnit); minimal coverage expected."
    lines.append(f"| Tests | `tests/` or `*Test.java` ({test_count} files) | {test_row_desc} | Keep → Expand |")

    # Build scripts
    build_count = len(report["build_files"])
    build_desc = "NetBeans/Ant markers and build metadata." if report["netbeans_present"] else "Build files discovered."
    lines.append(f"| Build scripts | `{', '.join(report['build_file_names'][:5]) or '—'}` | {build_desc} | Keep (reference) |")

    # Executables / jars
    jar_count = len(report["jar_files"])
    jar_list_preview = ", ".join(report["jar_file_names"][:3])
    jar_desc = f"{jar_count} jar(s) found ({jar_list_preview})" if jar_count else "No jars detected."
    lines.append(f"| Executables | `{', '.join(report['jar_file_names'][:3]) or '—'}` | {jar_desc} | Archive |")

    # Data stores
    txt_count = len(report["txt_files"])
    txt_preview = ", ".join([p.name for p in report["txt_files"][:3]])
    txt_desc = f"{txt_count} flat files discovered under Database/ or project roots ({txt_preview})."
    lines.append(f"| Data stores | `Database/*.txt` ({txt_count} files) | {txt_desc} | Replace → migrate to RDBMS |")

    # Docs
    docs = report['docs']
    docs_preview = ", ".join([d.name for d in docs[:3]]) if docs else "—"
    lines.append(f"| Docs | `Documentation/**`, `{docs_preview}` | Phase artefacts (Inception → Beta) | Keep, restructure |")

    lines.append("\n## 3. Module Inventory (auto-extracted from scanned Java files)\n")
    lines.append("| Module | Responsibility | Evidence / Dependencies | Notes |")
    lines.append("| --- | --- | --- | --- |")

    # Heuristics to map found classes to responsibilities
    # Look for obvious names
    modules = []
    name_to_path = report.get('java_class_map', {})
    # prioritize likely modules
    prioritize = ["POSSystem", "Employee", "Inventory", "Item", "POS", "PointOfSale", "Register", "Sale", "Rental", "ReturnItem"]
    for pname in prioritize:
        for path, cls in name_to_path.items():
            if cls and cls.lower() == pname.lower():
                modules.append((pname, path))
                break

    # fall back to scanning top classes with "UI" or Swing usage
    for path, cls in list(name_to_path.items())[:40]:
        if cls and cls not in [m[0] for m in modules]:
            modules.append((cls, path))

    # Emit up to ~12 modules
    for mod, path in modules[:12]:
        text = simple_read(ROOT / path)
        deps = []
        if "Inventory" in text or "itemDatabase" in text or ".txt" in text:
            deps.append("Inventory, text DB files")
        if SWING_IMPORTS.search(text):
            deps.append("Swing UI")
        if FILE_IO_PATTERNS.search(text):
            deps.append("Direct file IO")
        deps_str = ", ".join(deps) if deps else "See source"
        notes = []
        if SWING_IMPORTS.search(text):
            notes.append("UI class — likely direct IO from UI")
        if "singleton" in text.lower() or "getInstance" in text:
            notes.append("Singleton pattern observed")
        if not notes:
            notes.append("God-class / mixed responsibilities likely — needs review")
        lines.append(f"| `{mod}` | inferred responsibility from name | {deps_str} | {'; '.join(notes)} |")

    lines.append("\n## 4. Dependency Highlights")
    if report["netbeans_present"]:
        lines.append("- Build pipeline tied to NetBeans/Ant (`nbproject`, `build.xml`, `manifest.mf`) — no Gradle/Maven wrapper detected.")
    else:
        lines.append("- No NetBeans markers detected; build system not clearly tied to NetBeans.")

    if report["swing_usage"]:
        lines.append("- UI classes use Swing/AWT directly; UI and business logic are likely tightly coupled.")
    if report["file_io_usage"]:
        lines.append("- File-based persistence hard-coded via relative paths across multiple classes → strong coupling, no abstraction.")
    if report["logging_files"]:
        lines.append("- Logging appears to be file-appending (plain text logs) — no rotation or secure store detected.")
    lines.append("\n## 5. Asset Classification & Actions\n")
    lines.append("| Asset | Classification | Action |")
    lines.append("| --- | --- | --- |")

    # Use the classification mapping
    classification = report["classification"]
    # Pick representative assets to show in table
    shown = 0
    for asset, (atype, action) in list(classification.items()):
        lines.append(f"| `{asset}` | {atype} | {action} |")
        shown += 1
        if shown >= 20:
            break
    if len(classification) > 20:
        lines.append(f"| ... | ... | ... (and {len(classification)-20} more assets) |")

    lines.append("\n## 6. Risks & Gaps")
    # Environment
    if report["javac_present"]:
        lines.append("- **Environment**: `javac` found in PATH — Java available for tracing legacy app.")
    else:
        lines.append("- **Environment**: No `javac` found in PATH (`javac` missing) → must install JDK to run/trace legacy app before deeper reverse engineering.")
    # Data integrity
    if len(report["txt_files"]) > 0:
        lines.append("- **Data Integrity**: Text files lack schema constraints; duplicates or malformed entries likely when migrating.")
    # Testing debt
    if len(report["tests"]) < 3:
        lines.append("- **Testing Debt**: Minimal legacy tests found; need characterization tests before refactoring.")
    # Security
    if report["sensitive_hits"]:
        lines.append("- **Security**: Potential plaintext secrets/credentials detected in source files (search hits).")
    else:
        lines.append("- **Security**: No obvious plaintext password assignment patterns found via simple heuristics — manual review recommended.")

    lines.append("\n## 7. Immediate Next Steps")
    lines.append("1. Install/configure a JDK (8+) and document build instructions if `javac` absent.")
    lines.append("2. Add Gradle/Maven wrapper to standardize builds/tests outside the IDE (Gradle recommended).")
    lines.append("3. Write a high-level architecture diagram of current system using the findings above.")
    lines.append("4. Begin characterization tests around `POSSystem` login and `Inventory` operations to guard behavior pre-refactor.\n")

    # Add appendix: quick stats & files
    lines.append("### Appendix: Quick stats & notable files")
    lines.append(f"- Total files scanned: {report['total_files']}")
    lines.append(f"- Java files: {len(report['java_files'])}")
    lines.append(f"- Text DB files (*.txt): {len(report['txt_files'])}")
    lines.append(f"- JAR files: {len(report['jar_files'])}")
    lines.append(f"- Build files (Ant/NetBeans/Gradle/Maven): {', '.join(report['build_file_names']) or 'None found'}")
    lines.append("\n**Notable files found (preview):**")
    preview = report['notable_files'][:30]
    for p in preview:
        lines.append(f"- `{p}`")
    return "\n".join(lines)

def main():
    # Gather files
    all_files = [p for p in ROOT.rglob("*") if p.is_file()]
    java_files = [p for p in all_files if p.suffix == JAVA_EXT]
    txt_files = [p for p in all_files if p.suffix == TXT_EXT]
    jar_files = [p for p in all_files if p.suffix == JAR_EXT]
    docs = [p for p in all_files if p.suffix in {".md", ".docx", ".pdf", ".rst"} and "documentation" in p.parts or "doc" in p.name.lower() or "README" in p.name.upper()]

    # Tests heuristic: files with Test in name or under test folders
    tests = [p for p in all_files if ("test" in p.parts or re.search(r'Test\.java$', p.name))]
    build_files = [p for p in all_files if p.name in {"build.xml", "pom.xml", "settings.gradle", "build.gradle", "gradlew", "gradlew.bat", "manifest.mf"}]
    netbeans_folder = next((p for p in ROOT.iterdir() if p.is_dir() and p.name == "nbproject"), None)
    netbeans_present = netbeans_folder is not None or any(p.name in NETBEANS_MARKERS for p in all_files)

    # Detect classes and patterns
    java_class_map = detect_java_classes(java_files)
    swing_hits = detect_patterns(java_files, SWING_IMPORTS)
    file_io_hits = detect_patterns(java_files, FILE_IO_PATTERNS)
    txt_references = search_txt_references(java_files)

    # Sensitive content heuristic
    sensitive_hits = []
    for f in java_files + txt_files + docs:
        text = simple_read(f)
        for pat in SENSITIVE_PATTERNS:
            for m in pat.finditer(text):
                sensitive_hits.append((f.relative_to(ROOT).as_posix(), m.group(0)[:200]))

    # classification
    classification = classify_assets(all_files, java_files, txt_files, jar_files, tests, build_files, netbeans_folder)

    # javac presence
    javac_present = shutil.which("javac") is not None

    report = {
        "total_files": len(all_files),
        "java_files": java_files,
        "txt_files": txt_files,
        "jar_files": jar_files,
        "docs": docs,
        "tests": tests,
        "build_files": build_files,
        "build_file_names": [b.relative_to(ROOT).as_posix() for b in build_files],
        "netbeans_present": netbeans_present,
        "netbeans_folder": netbeans_folder,
        "java_class_map": java_class_map,
        "swing_usage": bool(swing_hits),
        "file_io_usage": bool(file_io_hits),
        "sensitive_hits": sensitive_hits,
        "classification": classification,
        "javac_present": javac_present,
        "notable_files": sorted([p.relative_to(ROOT).as_posix() for p in all_files], key=lambda s: s),
    }

    md = generate_markdown(report)

    try:
        with open(OUTPUT_MD, "w", encoding="utf-8") as f:
            f.write(md)
        print(f"Inventory analysis written to: {OUTPUT_MD}")
    except Exception as e:
        print("Error writing markdown:", e)

if __name__ == "__main__":
    main()
