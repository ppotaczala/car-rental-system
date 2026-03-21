import os
import subprocess
import requests

API_KEY = os.environ.get("OPENAI_API_KEY", "")

def read_changed_files():
    if not os.path.exists("changed_files.txt"):
        return []
    with open("changed_files.txt", "r", encoding="utf-8") as f:
        return [line.strip() for line in f if line.strip()]

def get_diff_for_files(files):
    if not files:
        return ""
    cmd = ["git", "diff", "HEAD~1", "HEAD", "--"] + files
    result = subprocess.run(cmd, capture_output=True, text=True, check=False)
    return result.stdout[:120000]

def call_openai(diff_text):
    if not API_KEY:
        return "OPENAI_API_KEY is not set."

    prompt = f"""
You are a senior Java reviewer.

Review this diff and return:
1. Risks
2. Code quality issues
3. Missing tests
4. Security concerns
5. Suggested improvements

Keep it practical and concise.

Diff:
{diff_text}
"""

    response = requests.post(
        "https://api.openai.com/v1/responses",
        headers={
            "Authorization": f"Bearer {API_KEY}",
            "Content-Type": "application/json",
        },
        json={
            "model": "gpt-5.4",
            "input": prompt
        },
        timeout=120
    )
    response.raise_for_status()
    data = response.json()

    chunks = []
    for item in data.get("output", []):
        for content in item.get("content", []):
            if content.get("type") == "output_text":
                chunks.append(content.get("text", ""))

    return "\n".join(chunks).strip()

def main():
    files = read_changed_files()
    if not files:
        print("# AI Review\n\nNo changed files detected.")
        return

    diff_text = get_diff_for_files(files)
    if not diff_text.strip():
        print("# AI Review\n\nNo diff content available.")
        return

    review = call_openai(diff_text)

    print("# AI Review\n")
    print("## Changed files")
    for f in files:
        print(f"- {f}")

    print("\n## Suggestions\n")
    print(review)

if __name__ == "__main__":
    main()