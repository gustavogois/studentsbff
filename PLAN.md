# StudentsBFF - Plano de Implementacao

## Escopo: Sprint 1-2 (Fundacao)

O foco inicial e construir a base do projeto: backend Spring Boot, frontend React, autenticacao JWT, CRUD de materias/topicos e perfis de estudante/pai.

---

## Estrutura do Projeto

```
studentsbff/
├── backend/                          # Spring Boot 3 + Java 21
│   ├── src/main/java/com/studentsbff/
│   │   ├── StudentsBffApplication.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java        # Spring Security + JWT filter
│   │   │   ├── JwtConfig.java             # JWT properties
│   │   │   └── CorsConfig.java
│   │   ├── auth/
│   │   │   ├── AuthController.java        # POST /api/auth/register, /api/auth/login
│   │   │   ├── AuthService.java
│   │   │   ├── JwtService.java            # Gerar/validar tokens
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   ├── dto/
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   └── AuthResponse.java
│   │   │   └── CustomUserDetailsService.java
│   │   ├── user/
│   │   │   ├── User.java                  # Entity: id, name, email, password, role, avatarUrl
│   │   │   ├── Role.java                  # Enum: STUDENT, PARENT
│   │   │   ├── UserRepository.java
│   │   │   ├── UserService.java
│   │   │   └── UserController.java        # GET /api/users/me
│   │   ├── student/
│   │   │   ├── Student.java               # Entity: id, user, grade, school
│   │   │   ├── StudentRepository.java
│   │   │   ├── StudentService.java
│   │   │   ├── StudentController.java     # GET/PUT /api/students/me
│   │   │   └── dto/
│   │   │       └── StudentDto.java
│   │   ├── parent/
│   │   │   ├── ParentStudent.java         # Entity: parentId, studentId (N:N)
│   │   │   ├── ParentStudentRepository.java
│   │   │   ├── ParentService.java
│   │   │   ├── ParentController.java      # GET /api/parents/students, POST /api/parents/link
│   │   │   └── dto/
│   │   │       └── LinkStudentRequest.java
│   │   └── subject/
│   │       ├── Subject.java               # Entity: id, name, student
│   │       ├── SubjectRepository.java
│   │       ├── SubjectService.java
│   │       ├── SubjectController.java     # CRUD /api/subjects
│   │       ├── Topic.java                 # Entity: id, name, subject, difficulty
│   │       ├── TopicRepository.java
│   │       ├── TopicService.java
│   │       ├── TopicController.java       # CRUD /api/subjects/{id}/topics
│   │       └── dto/
│   │           ├── SubjectRequest.java
│   │           ├── SubjectResponse.java
│   │           ├── TopicRequest.java
│   │           └── TopicResponse.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-dev.yml
│   │   └── db/migration/
│   │       └── V1__initial_schema.sql
│   ├── src/test/java/com/studentsbff/
│   │   ├── auth/AuthControllerTest.java
│   │   ├── subject/SubjectServiceTest.java
│   │   └── integration/
│   │       └── SubjectIntegrationTest.java  # TestContainers
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                          # React + TypeScript + Vite + PWA
│   ├── src/
│   │   ├── main.tsx
│   │   ├── App.tsx
│   │   ├── api/
│   │   │   └── client.ts              # Axios instance com JWT interceptor
│   │   ├── contexts/
│   │   │   └── AuthContext.tsx         # Login state, token storage
│   │   ├── pages/
│   │   │   ├── LoginPage.tsx
│   │   │   ├── RegisterPage.tsx
│   │   │   ├── DashboardPage.tsx       # Pagina inicial do estudante
│   │   │   ├── SubjectsPage.tsx        # Lista de materias
│   │   │   └── ParentDashboardPage.tsx # Visao do pai/mae
│   │   ├── components/
│   │   │   ├── Layout.tsx
│   │   │   ├── ProtectedRoute.tsx
│   │   │   ├── SubjectCard.tsx
│   │   │   └── TopicList.tsx
│   │   └── types/
│   │       └── index.ts
│   ├── index.html
│   ├── vite.config.ts
│   ├── tsconfig.json
│   └── package.json
├── docker-compose.yml                 # PostgreSQL para dev local
└── PLAN.md
```

---

## Tarefa 1: Inicializar projeto Spring Boot

**O que fazer:**
- Criar projeto Maven com Spring Boot 3.3+ e Java 21
- Dependencias: Spring Web, Spring Data JPA, Spring Security, PostgreSQL Driver, Flyway, Lombok, jjwt, Validation
- Configurar `application.yml` com perfis `dev` e `prod`
- Criar `docker-compose.yml` com PostgreSQL 16

**application.yml (dev):**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/studentsbff
    username: studentsbff
    password: studentsbff
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true

jwt:
  secret: dev-secret-key-min-256-bits-long-for-hs256
  expiration: 86400000  # 24h
```

**docker-compose.yml:**
```yaml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: studentsbff
      POSTGRES_USER: studentsbff
      POSTGRES_PASSWORD: studentsbff
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data
volumes:
  pgdata:
```

---

## Tarefa 2: Entidades e Migracoes

**Migracao V1__initial_schema.sql:**
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'PARENT')),
    avatar_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE students (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id),
    grade VARCHAR(20) NOT NULL,
    school VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE parent_students (
    parent_id UUID NOT NULL REFERENCES users(id),
    student_id UUID NOT NULL REFERENCES students(id),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (parent_id, student_id)
);

CREATE TABLE subjects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    student_id UUID NOT NULL REFERENCES students(id),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE topics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    subject_id UUID NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
    difficulty INTEGER NOT NULL DEFAULT 3 CHECK (difficulty BETWEEN 1 AND 5),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);
```

**Entidades JPA:** Cada tabela mapeada com `@Entity`, usando UUID como ID, `@CreationTimestamp` / `@UpdateTimestamp` para datas.

---

## Tarefa 3: Autenticacao JWT

**Fluxo:**
1. `POST /api/auth/register` — cria usuario + student/parent profile, retorna JWT
2. `POST /api/auth/login` — valida credenciais, retorna JWT
3. `JwtAuthenticationFilter` — intercepta requests, valida token, seta SecurityContext
4. `SecurityConfig` — endpoints publicos: `/api/auth/**`; demais requerem autenticacao

**Detalhes:**
- Senha com BCrypt
- JWT contendo: userId, email, role no payload
- Token expira em 24h
- Refresh token fica para uma iteracao futura

**Endpoints:**
| Metodo | Path | Body | Resposta |
|--------|------|------|----------|
| POST | /api/auth/register | {name, email, password, role, grade?, school?} | {token, user} |
| POST | /api/auth/login | {email, password} | {token, user} |
| GET | /api/users/me | - | {id, name, email, role} |

---

## Tarefa 4: CRUD de Materias e Topicos

**Endpoints de Subject:**
| Metodo | Path | Descricao |
|--------|------|-----------|
| GET | /api/subjects | Lista materias do estudante logado |
| POST | /api/subjects | Cria materia |
| GET | /api/subjects/{id} | Detalhe da materia |
| PUT | /api/subjects/{id} | Atualiza materia |
| DELETE | /api/subjects/{id} | Remove materia |

**Endpoints de Topic:**
| Metodo | Path | Descricao |
|--------|------|-----------|
| GET | /api/subjects/{subjectId}/topics | Lista topicos |
| POST | /api/subjects/{subjectId}/topics | Cria topico |
| PUT | /api/subjects/{subjectId}/topics/{id} | Atualiza topico |
| DELETE | /api/subjects/{subjectId}/topics/{id} | Remove topico |

**Regras:**
- Estudante so ve/edita suas proprias materias
- Pai pode ver materias dos filhos vinculados (somente leitura)
- Validacoes: nome obrigatorio, dificuldade 1-5

---

## Tarefa 5: Relacionamento Pai-Estudante

**Fluxo de vinculacao:**
1. Pai faz registro com role PARENT
2. Pai vincula com estudante via codigo/email: `POST /api/parents/link`
3. Pai consulta seus estudantes: `GET /api/parents/students`
4. Pai consulta materias de um estudante: `GET /api/parents/students/{studentId}/subjects`

**Regra:** Pai so acessa dados de estudantes vinculados a ele.

---

## Tarefa 6: Frontend React

**Setup:**
- Vite + React 18 + TypeScript
- React Router v6 para rotas
- Axios para chamadas HTTP (interceptor adiciona JWT do localStorage)
- CSS: Tailwind CSS ou CSS Modules (decisao simples — usar Tailwind)

**Rotas:**
| Path | Componente | Acesso |
|------|-----------|--------|
| /login | LoginPage | Publico |
| /register | RegisterPage | Publico |
| /dashboard | DashboardPage | STUDENT |
| /subjects | SubjectsPage | STUDENT |
| /subjects/:id | SubjectDetailPage | STUDENT |
| /parent | ParentDashboardPage | PARENT |

**AuthContext:**
- Armazena token e user no localStorage
- Fornece `login()`, `register()`, `logout()`
- `ProtectedRoute` redireciona para /login se nao autenticado

---

## Tarefa 7: Testes e Qualidade

**Backend:**
- **Unitarios (JUnit 5 + Mockito):**
  - `SubjectServiceTest` — CRUD logic, validacoes, permissoes
  - `AuthServiceTest` — registro, login, token generation
- **Integracao (TestContainers):**
  - `SubjectIntegrationTest` — testa controller → service → repo → PostgreSQL real
  - Usar `@SpringBootTest` + `@Testcontainers`

**Frontend:**
- Vitest + React Testing Library (setup basico, testes detalhados em sprints futuros)

---

## Ordem de Execucao

```
1. Inicializar backend (pom.xml, Application.java, docker-compose.yml)
2. Criar entidades + migracao Flyway
3. Implementar autenticacao JWT (Security, Filter, Controller)
4. Implementar CRUD de Subjects e Topics
5. Implementar vinculacao Parent-Student
6. Inicializar frontend (Vite, rotas, AuthContext, paginas basicas)
7. Adicionar testes unitarios e de integracao
```

Cada tarefa sera implementada sequencialmente nesta ordem. Tarefas 1-5 sao backend, tarefa 6 e frontend, tarefa 7 cruza ambos.

---

## Decisoes Tecnicas

| Decisao | Escolha | Motivo |
|---------|---------|--------|
| Build tool | Maven | Familiaridade do desenvolvedor, ampla documentacao |
| IDs | UUID | Seguranca, nao expoe sequencia |
| Migracoes | Flyway | Controle de schema versionado |
| JWT lib | jjwt (io.jsonwebtoken) | Madura, bem documentada |
| Senhas | BCrypt | Padrao Spring Security |
| Frontend styling | Tailwind CSS | Produtividade, sem setup complexo |
| Testes DB | TestContainers | Testa com PostgreSQL real |
| API prefix | /api/ | Separacao clara de rotas |
