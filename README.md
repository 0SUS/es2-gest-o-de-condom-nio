# es2-gest-o-de-condom-nio
Repositório para gerenciar o projeto da disciplina de engenharia de software 2 lecionada pelo professor Ricardo Argenton da Univasf

---

## 1. Visão Geral do Projeto

### 1.1 Descrição
Sistema de gestão de condomínio desenvolvido em Java para a disciplina de Engenharia de Software 2 da Universidade Federal do Vale do São Francisco (UNIVASF), sob orientação do professor Ricardo Argenton.

### 1.2 Objetivo
Automatizar e centralizar as operações administrativas de um condomínio, incluindo gestão de moradores, residências, reservas de áreas comuns, manutenções, taxas e comunicação interna.

### 1.3 Tipo de Aplicação
Aplicação desktop desenvolvida em Java Swing, utilizando arquitetura em camadas com acesso direto a banco de dados PostgreSQL.

---

## 2. Arquitetura e Tecnologias

### 2.1 Stack Tecnológico
- **Linguagem**: Java (JDK 24)
- **Interface Gráfica**: Java Swing (JFrame, JInternalFrame, JDesktopPane)
- **Banco de Dados**: PostgreSQL 42.7.8
- **Gerenciador de Dependências**: Maven
- **Padrão de Look and Feel**: Nimbus (quando disponível)

### 2.2 Estrutura de Pacotes
```
br.com.sistemaCondominio/
├── dal/          # Data Access Layer (Camada de Acesso a Dados)
│   ├── ModuloConexao.java      # Gerenciamento de conexão com BD
│   └── UsuarioLogado.java      # Singleton para usuário autenticado
├── dao/          # Data Access Object (vazio atualmente)
├── model/        # Modelos de dados (vazio atualmente)
├── telas/        # Interface gráfica (Telas do sistema)
└── icones/       # Recursos de ícones
```

---

## 3. Funcionalidades do Sistema

- **Autenticação e Controle de Acesso**: Login seguro com perfis de Administrador e Morador.
- **Gestão de Moradores**: Cadastro, edição e exclusão de moradores.
- **Gestão de Residências**: Cadastro, edição e exclusão de residências.
- **Reservas de Áreas Comuns**: Agendamento de áreas como piscina, churrasqueira e salão de festas.
- **Manutenções de Áreas Comuns**: Registro e acompanhamento de manutenções.
- **Gestão de Taxas**: Controle de taxas condominiais, com registro e status de pagamento.
- **Comunicação Interna**: Sistema de mensagens entre os usuários do sistema.

---

## 4. Como executar o projeto

### 4.1 Requisitos
- JDK 24 ou superior
- PostgreSQL instalado e configurado
- Maven configurado no ambiente

### 4.2 Configuração do Banco de Dados
1. Crie um banco de dados no PostgreSQL com o nome `sistema-condominio`.
2. Execute os scripts SQL localizados na pasta `sistemaCondomio/scripts/` para criar as tabelas necessárias.
3. Configure as credenciais do banco de dados no arquivo `sistemaCondomio/src/main/java/br/com/sistemaCondominio/dal/ModuloConexao.java`.

### 4.3 Executando a aplicação
1. Clone o repositório.
2. Abra o projeto em sua IDE de preferência (ex: IntelliJ IDEA, Eclipse).
3. A IDE deverá baixar as dependências do Maven automaticamente.
4. Execute a classe `TelaLogin.java` para iniciar a aplicação.

---

## 5. Banco de Dados

- **SGBD**: PostgreSQL
- **Host**: localhost
- **Porta**: 5432
- **Database**: sistema-condominio
- **Usuário**: postgres
- **Senha**: admin123 (hardcoded - **ATENÇÃO**: deve ser externalizada)

As principais tabelas do sistema são: `usuario`, `residencia`, `reservas_areas_comuns`, `manutencoes_areas_comuns`, `taxas` e `mensagens`.

---

## 6. Guia para Desenvolvedores

### 6.1 Convenções de Código
- **Nomenclatura**: Classes em PascalCase, métodos e variáveis em camelCase.
- **Estrutura de Telas**: Telas principais estendem `JInternalFrame` e são exibidas em um `JDesktopPane`.
- **Banco de Dados**: Utilize `PreparedStatement` para todas as queries SQL para prevenir SQL Injection.

### 6.2 Padrões a Seguir
- **Conexão**: Utilize `ModuloConexao.conector()` para obter uma conexão com o banco de dados.
- **Usuário Logado**: Utilize `UsuarioLogado.getInstance()` para acessar informações do usuário autenticado.
- **Validações**: Realize validações de campos obrigatórios e de regras de negócio antes de persistir os dados.
- **Tratamento de Erros**: Utilize `try-catch` para operações de banco de dados e exiba mensagens de erro ao usuário com `JOptionPane`.