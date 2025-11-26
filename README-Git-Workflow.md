# Git push rejected (non-fast-forward) and line endings (LF/CRLF) — Quick Fix

This project adds a .gitattributes at the repo root to normalize line endings and avoid "LF will be replaced by CRLF" warnings. Follow the steps below when your push is rejected because the remote has commits you don't have locally.

## 1) Make your local branch up-to-date
```
# Ensure you're on master (or main, depending on repo)
git checkout master

# Fetch and rebase to replay your commits on top of the remote branch
git fetch origin
git pull --rebase origin master
```
- If there are conflicts, resolve them in your editor, then continue:
```
git add -A
git rebase --continue
```
- If rebase becomes complicated, you can abort and try a regular merge:
```
git rebase --abort
git pull origin master
```

## 2) Normalize line endings (one-time after .gitattributes change)
The repo contains .gitattributes that enforces LF for source files. To apply normalization on your local checkout:
```
# Re-scan and normalize according to .gitattributes
git add --renormalize .

git commit -m "chore: normalize line endings per .gitattributes"
```
This prevents warnings like:
- "LF will be replaced by CRLF the next time Git touches it"

## 3) Push your changes
```
git push origin master
```
If you still get a rejection, repeat step 1 (there may be new remote commits).

## Optional: Configure Git line-endings locally
- Cross-platform teams typically prefer this repo setting plus:
```
# Recommended on Windows
git config --global core.autocrlf false
```
Reason: .gitattributes already handles normalization. Let Git store LF in the repo and your editor/IDE display endings as needed.

## Notes
- If your default branch is `main`, replace `master` with `main` in the commands above.
- The backend subfolder also contains a .gitattributes for IDEs that open that folder directly; the root file is authoritative.
