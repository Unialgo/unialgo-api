# Unialgo API

Unialgo é uma plataforma educacional projetada para auxiliar o ensino de programação por meio de desafios algorítmicos. Este repositório contém a API backend construída com Spring Boot que alimenta a plataforma Unialgo.

## Sumário
- [Visão Geral](#visão-geral)
- [Arquitetura](#arquitetura)
- [Padrões de Design e Princípios SOLID](#padrões-de-design-e-princípios-solid)
- [Tecnologias](#tecnologias)
- [Configuração e Instalação](#configuração-e-instalação)
- [Documentação da API](#documentação-da-api)

## Visão Geral

A API Unialgo fornece um backend robusto para uma plataforma de educação em programação onde:
- Professores podem criar e gerenciar atividades de programação
- Estudantes podem acessar e completar atividades
- Recursos baseados em IA auxiliam na geração de enunciados de problemas
- Avaliação automatizada de submissões de estudantes (em breve com integração Judge0)

O sistema foi projetado para ser escalável, manutenível e segue as melhores práticas em engenharia de software.

## Arquitetura

O projeto segue uma arquitetura em camadas com clara separação de responsabilidades:

### Componentes Principais

1. **Controllers**
    - Lidam com requisições e respostas HTTP
    - Mapeiam para métodos de serviço apropriados
    - Exemplos: `AIController`, `AssignmentController`, `QuestionController`

2. **Services**
    - Contêm a lógica de negócio
    - Orquestram operações entre controllers e repositories
    - Exemplos: `AIService`, `AssignmentService`, `QuestionService`

3. **Repositories**
    - Fornecem abstração de acesso a dados
    - Estendem o `CrudRepository` do Spring Data
    - Exemplos: `QuestionRepository`, `AssignmentRepository`

4. **Entities**
    - Representam objetos de domínio
    - Mapeiam para tabelas do banco de dados
    - Exemplos: `Question`, `Assignment`, `User`, `Teacher`, `Student`

5. **DTOs (Data Transfer Objects)**
    - Facilitam a troca de dados entre cliente e servidor
    - Previnem a exposição direta de entidades
    - Exemplos: `SaveQuestionRequestDto`, `GenerateStatementRequestDto`

## Padrões de Design e Princípios SOLID

### Padrões de Design

1. **Padrão MVC**
    - Clara separação entre controllers, services e repositories
    - Controllers processam requisições HTTP e delegam para services
    - Services contêm lógica de negócio e usam repositories para acesso a dados

2. **Padrão Repository**
    - Abstração sobre o armazenamento de dados
    - Promove testabilidade e acoplamento fraco
    - Implementação: Todas as interfaces de repositório estendem `CrudRepository`, como `UserRepository`, `TeacherRepository`, etc.

3. **Padrão DTO**
    - Troca segura de informações entre cliente e servidor
    - Previne exposição de entidades e potenciais problemas de segurança
    - Implementação: DTOs separados para requisições e respostas

4. **Injeção de Dependência**
    - Criação e ciclo de vida de objetos gerenciados pelo Spring
    - Injeção de dependências via construtor
    - Implementação: Services e controllers recebem dependências através de construtores

5. **Padrão Strategy**
    - Implementação de comportamentos específicos baseados em diferentes "roles" de usuário
    - Desacopla a lógica específica de cada papel (Teacher, Student) da UserService
    - Permite adicionar novos tipos de usuário sem modificar a lógica existente
    - Implementação: StudentService e TeacherService implementam comportamentos específicos

### Implementação de Princípios SOLID

1. **Princípio da Responsabilidade Única (SRP)**
    - Cada classe tem apenas um motivo para mudar
    - Clara separação entre controllers, services e repositories
    - Exemplo: `AIService` foca apenas em operações relacionadas à IA

2. **Princípio Aberto/Fechado (OCP)**
    - Classes são abertas para extensão, mas fechadas para modificação
    - Implementação: Interfaces de repositório estendem `CrudRepository`, permitindo métodos de consulta personalizados sem modificar a funcionalidade base

3. **Princípio da Substituição de Liskov (LSP)**
    - Subtipos devem ser substituíveis por seus tipos base
    - Implementação: Todos os repositories herdam de CrudRepository mantendo as relações de herança apropriadas, como `UserRepository`, `StudentRepository`, etc.

4. **Princípio da Segregação de Interface (ISP)**
    - Clientes não devem depender de interfaces que não utilizam
    - Implementação: Interfaces de repositório específicas para diferentes entidades em vez de uma interface genérica

5. **Princípio da Inversão de Dependência (DIP)**
    - Módulos de alto nível não devem depender de módulos de baixo nível; ambos devem depender de abstrações
    - Implementação: Services dependem de interfaces de repositório, não de implementações concretas

## Tecnologias

- **Spring Boot**: Framework principal para desenvolvimento da aplicação
- **Spring Data JPA**: Simplifica a implementação da camada de acesso a dados
- **Spring Security com OAuth2**: Gerencia autenticação e autorização
- **Keycloak**: Fornece gerenciamento de identidade e acesso
- **MySQL**: Banco de dados relacional para armazenamento de dados
- **Liquibase**: Migração de esquema de banco de dados
- **Docker/Docker Compose**: Containerização e orquestração
- **SpringDoc (Swagger)**: Documentação da API
- **Maven**: Gerenciamento de dependências e ferramenta de build
- **JDK 21**: Kit de Desenvolvimento Java

## Configuração e Instalação

### Pré-requisitos
- JDK 21 ou superior
- Docker e Docker Compose
- Maven

### Passos para Executar

1. **Clonar o Repositório**
   ```bash
   git clone https://github.com/your-username/unialgo-api.git
   cd unialgo-api
   ```

2. **Iniciar Serviços Docker**
   ```bash
   docker-compose up -d
   ```
   Isso inicia:
    - Banco de dados MySQL
    - Servidor de autenticação Keycloak (com realm pré-configurado)

3. **Compilar e Executar a Aplicação**
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

4. **Acessar a Aplicação**
    - Endpoint da API: http://localhost:8080
    - Swagger UI: http://localhost:8080/public/swagger.html
    - Admin do Keycloak: http://localhost:8180 (admin/unialgo)

> **Nota Importante**: A configuração atual tem algumas limitações:
> - O Liquibase pode precisar ser executado manualmente caso não seja inicializado automaticamente com o projeto
> - O arquivo de configuração do Keycloak pode estar desatualizado, o que pode causar problemas durante o cadastro

## Documentação da API

A API é documentada usando OpenAPI (Swagger) que fornece:
- Documentação interativa da API
- Exemplos de requisição/resposta
- Informações de autenticação
- Esquemas de modelo

Acesse o Swagger UI em: http://localhost:8080/public/swagger.html após iniciar a aplicação.

## Melhorias Futuras

- **Integração com Judge0**: Para avaliação automatizada de código
- **Capacidades Expandidas de IA**: Geração aprimorada de enunciados de problemas
- **Métricas de Desempenho**: Para acompanhamento do progresso dos estudantes
- **Recursos Avançados de Atividades**: Desafios de código com restrições de tempo