# Evidenceframe

Evidenceframe is a read-only CLI tool that collects verifiable technical evidence
for compliance audits (ISO/IEC 27001, vendor security reviews, internal audits).

It produces:
- Human-readable summaries
- Machine-readable metadata
- Cryptographic hashes
- A single ZIP artifact for auditors

No systems are modified. No evidence is overwritten.

---

## What this tool does

- Collects evidence using read-only access
- Writes deterministic, auditable files
- Refuses to overwrite existing evidence
- Produces verifiable hashes (SHA-256)
- Exits with CI/CD-friendly exit codes

---

## What this tool does NOT do

- No compliance scoring
- No policy interpretation
- No write access
- No background agents
- No hidden behavior

---

## Installation

Download the native binary for your platform and make it executable:

```bash
chmod +x evidenceframe
