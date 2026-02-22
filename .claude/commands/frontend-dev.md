Apply frontend development conventions for StudentsBFF (React 19 / TypeScript / Vite / Tailwind CSS / PWA).

## Important: Frontend Explanation Style

The user has **limited frontend experience** (unlike the backend). Every frontend task must include:

1. **What** is being created or changed — describe the file, component, or concept.
2. **Why** — explain the design decision: why this approach, why this file structure, why this library.
3. **How to test manually** — describe step-by-step what to do in the browser to verify the feature works.

Never assume frontend knowledge. Prefer clear, concrete explanations over jargon.

---

## Frontend Project Structure (`frontend/src/`)

```
src/
├── main.tsx          — app entry point; mounts <App /> into the DOM
├── index.css         — global styles; Tailwind @layer directives
├── App.tsx           — root component; holds routing (React Router)
├── pages/            — one component per route (e.g. DashboardPage, SubjectsPage)
├── components/       — reusable UI components (e.g. Button, Modal, SubjectCard)
│   └── ui/           — purely visual primitives (no business logic)
├── hooks/            — custom React hooks (e.g. useSubjects, useAuth)
├── services/         — API call functions (e.g. subjectService.ts, authService.ts)
├── contexts/         — React contexts (e.g. AuthContext.tsx)
├── types/            — shared TypeScript interfaces and types
└── utils/            — pure utility functions (date formatting, string helpers)
```

---

## Tech Stack Conventions

### TypeScript
- All files use `.tsx` for components, `.ts` for non-JSX modules
- Prefer explicit types for function parameters and return values
- Use `interface` for object shapes, `type` for unions/aliases
- Avoid `any`; use `unknown` if the type is truly unknown and narrow it before use

### React 19 Components
- Use **function components** exclusively — no class components
- Use `const` arrow functions for components:
  ```tsx
  const SubjectCard = ({ subject }: SubjectCardProps) => {
    return <div>{subject.name}</div>;
  };
  export default SubjectCard;
  ```
- Each component lives in its own file, named with PascalCase
- Keep components small and focused. If a component exceeds ~100 lines, consider splitting it

### State Management
- Use React built-in hooks first: `useState`, `useReducer`, `useContext`
- Extract complex or reusable state logic into custom hooks in `hooks/`
- Avoid introducing a third-party state library unless clearly necessary

### API Calls (Services Layer)
- All HTTP calls live in `services/`, never inside components directly
- Each service file corresponds to a backend resource:
  ```
  services/authService.ts
  services/subjectService.ts
  services/studentService.ts
  ```
- Functions return typed responses:
  ```ts
  export async function getSubjects(): Promise<Subject[]> { ... }
  ```

### TypeScript Types (`types/`)
- Mirror backend DTOs with frontend interfaces
- Keep types close to their domain; group by resource

### Tailwind CSS
- Use Tailwind utility classes directly in JSX — no separate CSS files per component
- Avoid arbitrary values unless absolutely necessary; prefer Tailwind's scale
- Keep global styles in `index.css` minimal — only Tailwind directives and genuine resets

---

## Development Commands

```bash
cd frontend && npm run dev      # Start dev server (hot reload)
cd frontend && npx tsc --noEmit # Type-check without building
cd frontend && npm run build    # Build for production
cd frontend && npm test         # Run unit tests (Vitest)
cd frontend && npm run lint     # Lint
```

> Dev server runs on http://localhost:5173 by default.
> Backend API runs on http://localhost:8080.

---

## Manual Testing Checklist (for each frontend task)

After every change, explain to the user:

1. Which URL to visit in the browser
2. What action to take (click, type, navigate)
3. What the expected visual result is
4. What the browser's DevTools Network tab should show (API calls, status codes)
5. Any error states to verify (empty list, API down, validation failures)
