# TecnoOrdem Pro — Gestão de Assistências Técnicas

**Slogan:** *Sua assistência técnica, sob controle.*

O **TecnoOrdem Pro** é um aplicativo Android comercial completo, robusto e nativo, desenvolvido em Kotlin e Jetpack Compose com Material Design 3, arquitetura MVVM, banco de dados local com Room e geração nativa de documentos em PDF. O sistema foi projetado para a gestão de assistências técnicas de smartphones, computadores, notebooks, tablets, impressoras, TVs, consoles de videogame e eletrodomésticos.

---

## 🚀 Principais Módulos & Recursos

1. **Dashboard em Tempo Real:**
   - Indicadores operacionais (Ordens Abertas, Em Reparo, Prontas para Retirada, Concluídas).
   - Indicadores financeiros (Receita realizada, despesas operacionais, lucro líquido estimado e saldos a receber).
   - Alertas rápidos de estoque baixo e garantias ativas.

2. **Gestão Completa de Ordens de Serviço (OS):**
   - Numeração sequencial automática com histórico de status auditável.
   - Vinculação com clientes e múltiplos equipamentos.
   - Diagnóstico técnico, serviços executados, peças aplicadas, mão de obra, descontos e cálculo de sinal/saldo pendente.
   - Duplicação de ordens para serviços recorrentes.

3. **Garantias & Conformidade com o CDC (Lei nº 8.078/1990):**
   - Emissão de Garantia Legal (Art. 26 do CDC - 90 dias para bens duráveis) e garantias contratuais.
   - Contagem precisa a partir da data de entrega do equipamento reparado.

4. **Geração de Documentos em PDF & Impressão Real via Wi-Fi:**
   - Geração local A4 via `android.graphics.pdf.PdfDocument`.
   - Modelos: Ordem de Serviço, Comprovante de Entrada, Orçamento Comercial, Certificado de Garantia e Termo de Quitação.
   - Impressão nativa via `PrintManager` (sem dependência de serviços externos).
   - Compartilhamento no WhatsApp e Android Sharesheet via `FileProvider`.

5. **Controle de Estoque e Peças:**
   - Peças com código SKU, código de barras, localização de bancada/gaveta, custo e preço de venda.
   - Alerta visual de estoque mínimo e baixa automática.

6. **Módulo Financeiro:**
   - Contas a receber e a pagar, fluxo de caixa, formas de pagamento (Pix, Dinheiro, Cartões de Débito/Crédito).

7. **Personalização da Assistência Técnica:**
   - Nome fantasia, razão social, CNPJ/CPF, endereço, responsável técnico, dados de Pix e termos contratuais personalizados.

---

## 🛠️ Tecnologias e Arquitetura

- **Linguagem:** Kotlin 2.2.10
- **UI:** Jetpack Compose + Material Design 3
- **Arquitetura:** MVVM (Model-View-ViewModel) + Repository Pattern
- **Banco de Dados Local:** Room Database 2.7.0 com KSP
- **Coroutines & Flow:** Reatividade e execução assíncrona
- **Impressão:** Android Print Framework (`PrintManager` & `PrintDocumentAdapter`)
- **Compartilhamento:** Android FileProvider (`content://` URIs)
- **Testes:** Robolectric 4.16.1 e JUnit 4

---

## 📦 Como Compilar e Executar no Android Studio

1. **Requisitos:**
   - Android Studio Ladybug (ou superior).
   - JDK 17 ou 21 configurado no Gradle.
   - Android SDK Platform 34+ instalado.

2. **Abrir o Projeto:**
   - No Android Studio, selecione `File -> Open` e aponte para a pasta raiz do projeto.
   - Aguarde a sincronização inicial do Gradle.

3. **Execução de Testes Automatizados:**
   Execute no terminal ou na janela Gradle:
   ```bash
   gradle :app:testDebugUnitTest
   ```

4. **Gerar APK de Teste:**
   ```bash
   gradle :app:assembleDebug
   ```
   O APK será gerado em: `app/build/outputs/apk/debug/app-debug.apk`

---

## 🔐 Configuração de Assinatura para Release (Google Play Store)

Para publicar na Google Play Store, gere seu keystore de publicação:

1. **Gerar Keystore:**
   ```bash
   keytool -genkey -v -keystore release.keystore -alias tecnoordem -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Configurar Variáveis de Ambiente:**
   - `KEYSTORE_PATH`: caminho absoluto para o seu arquivo `release.keystore`.
   - `STORE_PASSWORD`: senha do keystore.
   - `KEY_PASSWORD`: senha da chave.

3. **Gerar o Android App Bundle (AAB) para a Play Store:**
   ```bash
   gradle :app:bundleRelease
   ```
   O artefato final será gerado em:
   `app/build/outputs/bundle/release/app-release.aab`
