import sys

with open("app/src/main/java/com/example/universalvideodownloader/ui/main/MainScreen.kt", "r") as f:
    text = f.read()

def check_parens(s):
    stack = []
    for i, c in enumerate(s):
        if c in '({[':
            stack.append((c, i))
        elif c in ')}]':
            if not stack:
                print(f"Unmatched closing {c} at {i}")
                return
            top, pos = stack.pop()
            if (top == '(' and c != ')') or (top == '{' and c != '}') or (top == '[' and c != ']'):
                print(f"Mismatched {c} at {i}. Expected to close {top} from {pos}")
                return
    if stack:
        print("Unclosed braces/parens:")
        for c, pos in stack:
            print(f"{c} at {pos}")
    else:
        print("All parens matched perfectly.")

check_parens(text)
