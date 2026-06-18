# Rest Assured — Slides

Apresentação Reveal.js sobre Rest Assured (API testing, clients, JUnit 5, rest-assured Petclinic).

## Arquivos

| Arquivo | Descrição |
|---------|-----------|
| `index.html` | Apresentação interativa (Reveal.js) |
| `css/theme-rest-assured.css` | Tema visual Rest Assured |
| `assets/rest-assured-logo.svg` | Logo do projeto |
| `assets/icons/` | Ícones de marcas (Java, Maven, Docker, Git, etc.) via [Simple Icons](https://simpleicons.org/) |
| `css/icons.css` | Estilos compartilhados para ícones em guias e slides |

## Outros materiais

Walkthroughs de treinamento (bloco a bloco por classe de teste): [`docs/`](../) · [English](../en/README.md) · [Português](../pt/README.md)

Guias completos (página única HTML): [`guia-completo.html`](../guia-completo.html) (PT) · [`complete-guide.html`](../complete-guide.html) (EN)

## Visualizar no browser

```bash
npm run slides
# http://localhost:3335/docs/slides/              ← slides Reveal.js
# http://localhost:3335/docs/guia-completo.html   ← guia PT
# http://localhost:3335/docs/complete-guide.html  ← guide EN
# http://localhost:3335/docs/                     ← landing page
```

Abrir direto:

```bash
npm run slides:open
```

## Regenerar PDF

```bash
npm run slides:pdf
```

Gera `docs/slides/rest-assured-intro-slides.pdf` via [decktape](https://github.com/astefanutti/decktape).
