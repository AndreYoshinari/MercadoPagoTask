# MercadoPagoTask
Task prática Kotlin (SmartPOS e SDK)

##📲 Visão Geral

Este é um aplicativo Android que demonstra a integração com a API do Mercado Pago para processamento de pagamentos. 
O app permite criar preferências de pagamento e gerar uma URL de pagamento.

##🚀 Como Rodar o Projeto

### Pré-requisitos

- Android Studio (versão mais recente recomendada)
- JDK 11 ou superior
- Dispositivo Android ou emulador com API nível 24 ou superior
- Acesso à internet para chamadas à API

### Passos
1. Clone o repositório:
    git clone https://github.com/seu-usuario/nome-do-repositorio.git

2. Abra o projeto no Android Studio.

3. Sincronize o projeto com o Gradle.

4. Execute o projeto em um dispositivo ou emulador.

##🔧 Principais Componentes e Estrutura

### PreferenceRequest

Classe responsável por fazer a comunicação com a API do Mercado Pago

Implementa a criação de preferências de pagamento

Trata erros e respostas da API

### PagamentoActivity

Tela principal com formulário de pagamento

Implementa máscara monetária para o campo de valor

Validação em tempo real dos campos

Integração com Custom Tabs para o checkout

### PaymentResultActivity (não implementado)

Trata o retorno do pagamento via Deep Link

Exibe o resultado do pagamento para o usuário

### Dependências Principais

Retrofit: Para chamadas HTTP à API do Mercado Pago

Coroutines: Para operações assíncronas

Material Components: Para UI moderna

Browser: Para integração com Custom Tabs


## 💭 Considerações

Por não ser um projeto que especifica arquitetura ou estrutura na descrição, optei por utilizar conceitos mais básicos e diretos, como trabalhar com Activity em vez de Fragment. 
Além disso, não utilizei injeção de dependência (normalmente utilizo o Koin), arquitetura (MVVM) e também não fiz commits elaborados (como usar feat:, build:, fix:, etc.).
