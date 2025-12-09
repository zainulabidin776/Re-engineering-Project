import json

def generate_document(data):
    output = []

    # Header
    output.append(f"# Dual Documentation: Legacy ↔ Reengineered System")
    output.append(f"## Complete Comparison and Mapping\n")
    output.append("This document provides comprehensive comparison between the legacy system and the reengineered system, including architecture, data models, features, and code mapping.\n")
    output.append("---\n")

    # Table of Contents
    output.append("## Table of Contents\n")
    toc_sections = [
        "Architecture Comparison",
        "Data Model Comparison",
        "Feature Mapping",
        "Code Structure Mapping",
        "Technology Stack Comparison",
        "Workflow Comparison",
        "Deployment Comparison",
        "Justification of Changes"
    ]
    for i, section in enumerate(toc_sections, 1):
        output.append(f"{i}. [{section}](#{section.lower().replace(' ', '-')})")
    output.append("\n---\n")

    # ------------------------------------------
    # 1. Architecture Comparison
    # ------------------------------------------
    output.append("## Architecture Comparison\n")

    # Legacy Architecture Diagram
    output.append("### Legacy Architecture\n")
    output.append("```\n" + data["architecture"]["legacy_diagram"] + "\n```\n")
    output.append("**Characteristics:**")
    for item in data["architecture"]["legacy_characteristics"]:
        output.append(f"- {item}")
    output.append("")

    # Reengineered Architecture Diagram
    output.append("### Reengineered Architecture\n")
    output.append("```\n" + data["architecture"]["new_diagram"] + "\n```\n")
    output.append("**Characteristics:**")
    for item in data["architecture"]["new_characteristics"]:
        output.append(f"- {item}")
    output.append("")

    # Architecture Comparison Table
    output.append("### Architecture Comparison Table\n")
    output.append("| Aspect | Legacy System | Reengineered System |")
    output.append("|--------|---------------|---------------------|")
    for row in data["architecture"]["comparison"]:
        output.append(f"| {row['aspect']} | {row['legacy']} | {row['new']} |")
    output.append("\n---\n")

    # ------------------------------------------
    # 2. Data Model Comparison
    # ------------------------------------------
    output.append("## Data Model Comparison\n")

    output.append("### Legacy Data Storage\n")
    output.append("**File-Based Storage:**")
    for f in data["data_model"]["legacy_files"]:
        output.append(f"- `{f}`")
    output.append("")

    output.append("**Characteristics:**")
    for c in data["data_model"]["legacy_characteristics"]:
        output.append(f"- {c}")
    output.append("")

    output.append("### Reengineered Data Storage\n")
    output.append("**Database Schema:**")
    for t in data["data_model"]["new_tables"]:
        output.append(f"- `{t}`")
    output.append("")

    output.append("**Characteristics:**")
    for c in data["data_model"]["new_characteristics"]:
        output.append(f"- {c}")
    output.append("")

    # Data Mapping Table
    output.append("### Data Model Mapping")
    output.append("| Legacy File | Legacy Format | Reengineered Table(s) | Transformation |")
    output.append("|-------------|---------------|----------------------|----------------|")
    for row in data["data_model"]["mapping"]:
        output.append(f"| {row['legacy_file']} | {row['legacy_format']} | {row['new_tables']} | {row['transformation']} |")
    output.append("\n---\n")

    # ------------------------------------------
    # 3. Feature Mapping
    # ------------------------------------------
    output.append("## Feature Mapping\n")
    output.append("| Feature | Legacy Implementation | Reengineered Implementation | Status |")
    output.append("|---------|----------------------|-----------------------------|--------|")
    for row in data["features"]["comparison"]:
        output.append(f"| {row['feature']} | {row['legacy']} | {row['new']} | {row['status']} |")
    output.append("\n---\n")

    # ------------------------------------------
    # 4. Code Structure Mapping
    # ------------------------------------------
    output.append("## Code Structure Mapping\n")
    output.append("### Class to Component Mapping\n")
    output.append("| Legacy Class | Legacy Responsibility | Reengineered Component | Type | Justification |")
    output.append("|--------------|----------------------|------------------------|------|---------------|")
    for row in data["code_mapping"]["class_mapping"]:
        output.append(f"| {row['legacy_class']} | {row['legacy_responsibility']} | {row['new_component']} | {row['type']} | {row['justification']} |")

    output.append("\n### Package Structure Mapping\n")
    output.append("**Legacy Structure:**")
    output.append("```\n" + data["code_mapping"]["legacy_structure"] + "\n```")
    output.append("**Reengineered Structure:**")
    output.append("```\n" + data["code_mapping"]["new_structure"] + "\n```")
    output.append("\n---\n")

    # ------------------------------------------
    # 5. Technology Stack Comparison
    # ------------------------------------------
    output.append("## Technology Stack Comparison\n")
    output.append("| Category | Legacy System | Reengineered System | Justification |")
    output.append("|----------|---------------|---------------------|---------------|")
    for row in data["technology"]["stack"]:
        output.append(f"| {row['category']} | {row['legacy']} | {row['new']} | {row['justification']} |")
    output.append("\n---\n")

    # ------------------------------------------
    # 6. Workflow Comparison
    # ------------------------------------------
    output.append("## Workflow Comparison\n")
    for workflow in data["workflow"]:
        output.append(f"### {workflow['name']}\n")
        output.append("**Legacy:**")
        output.append("```\n" + workflow["legacy"] + "\n```")
        output.append("**Reengineered:**")
        output.append("```\n" + workflow["new"] + "\n```")
        output.append("**Improvements:**")
        for imp in workflow["improvements"]:
            output.append(f"- {imp}")
        output.append("")

    output.append("---\n")

    # ------------------------------------------
    # 7. Deployment Comparison
    # ------------------------------------------
    output.append("## Deployment Comparison\n")
    output.append("| Aspect | Legacy System | Reengineered System |")
    output.append("|--------|---------------|---------------------|")
    for row in data["deployment"]:
        output.append(f"| {row['aspect']} | {row['legacy']} | {row['new']} |")
    output.append("\n---\n")

    # ------------------------------------------
    # 8. Justification of Changes
    # ------------------------------------------
    output.append("## Justification of Changes\n")
    for item in data["justification"]:
        output.append(f"### {item['title']}\n")
        for b in item["benefits"]:
            output.append(f"- **{b}**")
        output.append("\n**Evidence:** " + item["evidence"] + "\n")

    return "\n".join(output)


# ------------------------------
# MAIN SCRIPT
# ------------------------------
if __name__ == "__main__":
    with open("restructure_input.json", "r") as f:
        data = json.load(f)

    md_output = generate_document(data)

    with open("Final_Reengineered_Document.md", "w", encoding="utf-8") as f:
        f.write(md_output)

    print("\n✅ Document generated successfully: Final_Reengineered_Document.md")
