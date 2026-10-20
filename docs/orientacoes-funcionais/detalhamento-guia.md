# Plano de refatoração — Orientações Funcionais

A refatoração deve transformar o cadastro atual — nome, descrição, finalidade, tag e imagem — em um **catálogo de orientações revisadas**, com instruções claras, cuidados e apresentação adequada ao participante. O registro voluntário de realização continua separado do catálogo e não entra no cálculo de adesão alimentar.

Abaixo está uma sequência de implementação. Os campos e regras propostos ainda precisam ser validados com os profissionais responsáveis pelo conteúdo antes da publicação das orientações.

## Etapa 1 — Fechar as regras do módulo

Antes de alterar o banco, documente estas regras:

1. **Orientação funcional** é um conteúdo geral do catálogo, não uma prescrição individual.
2. O participante pode consultar orientações publicadas e registrar voluntariamente uma realização.
3. Orientações funcionais não geram atividades “pendentes” nem percentual de adesão.
4. Somente conteúdos revisados podem ser publicados.
5. Inativar uma orientação impede novas consultas no catálogo, mas preserva os registros de realização existentes.
6. Tags servem para organizar e localizar conteúdos. Elas não determinam automaticamente se uma atividade é adequada para uma pessoa.

**Decisão a confirmar com a equipe:** quem pode criar, revisar, publicar e inativar uma orientação. Para o piloto, uma regra simples seria permitir que profissionais autorizados criem rascunhos e que um responsável designado faça a publicação.

## Etapa 2 — Definir o conteúdo de uma orientação

Recomendo dividir o formulário em cinco seções.

| Seção | Campos | Comportamento |
|---|---|---|
| Identificação | Nome, resumo, finalidade, categoria | O resumo aparece no cartão do catálogo; a finalidade explica o propósito ao abrir a orientação. |
| Execução | Passos ordenados, duração estimada e materiais, quando aplicáveis | O participante vê uma sequência clara de instruções. Duração e materiais não devem ser obrigatórios para toda atividade. |
| Cuidados | Precauções, situações em que não se deve realizar, sinais de alerta | Cada grupo aparece separado e pode conter vários itens. O texto deve ser definido ou revisado pelo profissional responsável. |
| Mídia | Imagem de capa e imagens adicionais | Imagens adicionais têm ordem e legenda; quando possível, são associadas a um passo. |
| Organização e publicação | Tags, status e dados da revisão | Controla busca, filtros e disponibilidade ao participante. |

### Como tratar os três grupos de cuidados

- **Precauções:** cuidados ou adaptações durante a execução.
- **Situações em que não se deve realizar:** condições nas quais a orientação não deve ser seguida sem avaliação individual, conforme definição profissional.
- **Sinais de alerta:** situações percebidas durante a atividade que indicam interrompê-la e seguir a orientação de atendimento definida pela equipe.

Implemente cada grupo como uma **lista de textos ordenados**, com “Adicionar item” e “Remover item”. Isso torna a leitura melhor do que três caixas de texto extensas.

Um campo vazio deve aparecer internamente como **não preenchido ou não revisado**. O sistema não deve converter a ausência de informação em uma afirmação como “não possui contraindicações”.

## Etapa 3 — Organizar categorias e tags

A tag “Equilíbrio” vista na tela atual precisa de um significado consistente. Sugiro:

- **Categoria principal:** assunto predominante da orientação, como equilíbrio, mobilidade ou fortalecimento.
- **Tags complementares:** características úteis para busca, como “sem equipamento” ou “sentado”.
- **Finalidade:** explicação textual do objetivo; não precisa ser uma tag.

Comece com uma lista pequena, validada pela equipe, e cadastre-a no banco. No formulário, use seleção múltipla das tags existentes. Um CRUD administrativo pode permitir criar, renomear e inativar opções, mas **não permita criar tags livres durante o cadastro da orientação** na primeira versão. Isso evita duplicatas e sinônimos.

Não é necessário criar um CRUD para precauções, sinais de alerta e materiais agora. Esses textos variam conforme a orientação e devem permanecer no próprio cadastro. Uma biblioteca de frases reutilizáveis só se justifica se a equipe demonstrar essa necessidade no uso real.

## Etapa 4 — Refatorar o modelo de dados

Uma estrutura inicial suficiente seria:

```text
OrientacaoFuncional
- id
- nome
- resumo
- finalidade
- categoria_id
- duracao_estimada (opcional)
- materiais (opcional)
- status: RASCUNHO | PUBLICADA | INATIVA
- criado_por
- revisado_por
- criado_em
- atualizado_em
- publicado_em

PassoOrientacao
- id
- orientacao_id
- ordem
- instrucao

CuidadoOrientacao
- id
- orientacao_id
- tipo: PRECAUCAO | NAO_REALIZAR | SINAL_DE_ALERTA
- ordem
- texto

MidiaOrientacao
- id
- orientacao_id
- passo_id (opcional)
- ordem
- arquivo_id ou chave_armazenamento
- legenda
- texto_alternativo
- capa

CategoriaOrientacao
- id
- nome
- ativo

TagOrientacao
- id
- nome
- grupo (opcional)
- ativo

OrientacaoTag
- orientacao_id
- tag_id
```

**Mantenha a entidade de realização separada.** Ela registra o que o participante informou ter feito, com data, duração, sensação final e observação, conforme as regras já existentes. Não replique essas informações no cadastro da orientação.

### Migração dos dados atuais

Faça a transição sem descartar os cadastros existentes:

1. crie as novas tabelas e campos;
2. migre a descrição atual para `resumo` ou para um primeiro passo, conforme seu conteúdo;
3. migre a imagem atual como imagem de capa;
4. associe as tags atuais às novas opções controladas;
5. mantenha os registros antigos como rascunho até revisar instruções e cuidados;
6. preserve os IDs das orientações que já possuem realizações vinculadas.

A migração da descrição merece revisão manual: um texto descritivo não se torna automaticamente uma instrução de execução adequada.

## Etapa 5 — Implementar imagens múltiplas

No editor, permita:

- escolher uma imagem de capa;
- adicionar outras imagens;
- ordenar as imagens;
- incluir legenda e texto alternativo;
- associar uma imagem a um passo, quando fizer sentido;
- remover ou substituir uma imagem.

No backend, valide tipo, tamanho e quantidade de arquivos. Guarde os arquivos no armazenamento escolhido e, no banco, apenas seus identificadores e metadados. Para o piloto, estabeleça um limite simples de imagens por orientação; a equipe pode ajustá-lo após cadastrar exemplos reais.

## Etapa 6 — Implementar o fluxo de edição e publicação

O formulário administrativo pode seguir esta ordem:

1. **Identificação:** nome, resumo, finalidade e categoria.
2. **Execução:** passos, duração e materiais.
3. **Cuidados:** os três grupos separados.
4. **Imagens:** capa e ilustrações dos passos.
5. **Organização:** tags.
6. **Prévia:** visualização como participante.
7. **Revisão e publicação.**

Permita salvar rascunhos incompletos. Para publicar, valide ao menos nome, resumo, instruções, categoria, revisão dos cuidados e responsável pela revisão. A equipe deve decidir se a imagem de capa será obrigatória; eu a recomendaria para manter o catálogo compreensível visualmente.

Quando instruções ou cuidados de uma orientação publicada forem alterados, registre **quem alterou e quando**. Para a primeira versão, é possível editar e submeter novamente à revisão, preservando um histórico básico de alterações. Um sistema completo de versões pode vir depois, mas não convém perder o conteúdo anterior sem registro.

## Etapa 7 — Refatorar a experiência do participante

O catálogo deve mostrar cartões com **nome, resumo, imagem de capa e categoria**. Ao abrir uma orientação, a página deve apresentar:

1. finalidade;
2. materiais e duração, se informados;
3. passos com suas imagens;
4. precauções;
5. situações em que não se deve realizar;
6. sinais de alerta;
7. ação para registrar uma realização voluntária.

Use termos como **“Orientações funcionais”** e **“Atividades realizadas”**. A navegação da tela ainda usa “Exercícios”, enquanto o formulário já fala em orientação funcional; vale alinhar essa nomenclatura durante a refatoração.

Os cuidados devem estar visíveis antes da ação de registrar a realização. A interface não deve sugerir que o sistema avaliou individualmente se a atividade é segura para aquele participante.

## Etapa 8 — Ajustar API e validações

Operações previstas:

```http
GET    /orientacoes-funcionais
GET    /orientacoes-funcionais/{id}

POST   /orientacoes-funcionais
PUT    /orientacoes-funcionais/{id}
POST   /orientacoes-funcionais/{id}/publicar
POST   /orientacoes-funcionais/{id}/inativar

POST   /orientacoes-funcionais/{id}/midias
DELETE /orientacoes-funcionais/{id}/midias/{midiaId}

GET    /categorias-orientacao
GET    /tags-orientacao
```

Os caminhos podem seguir o padrão já usado no projeto. As regras relevantes são:

- a consulta do participante retorna somente orientações publicadas;
- o administrador ou profissional autorizado pode consultar rascunhos;
- apenas perfis autorizados editam e publicam;
- a publicação executa as validações de conteúdo;
- a inativação não apaga realizações históricas;
- a ordem dos passos, cuidados e imagens é persistida;
- arquivos são acessados conforme a política de acesso do sistema.

## Ordem de execução recomendada

| Prioridade | Entrega | Critério de conclusão |
|---|---|---|
| **P0** | Validar 3 a 5 orientações reais com a equipe | Campos, categorias e formato dos cuidados confirmados. |
| **P0** | Criar modelo de passos, cuidados, categorias e status | Migração preserva orientações e realizações existentes. |
| **P0** | Refatorar editor e regras de publicação | É possível salvar rascunho, revisar, visualizar prévia e publicar. |
| **P0** | Refatorar visualização do participante | Instruções e cuidados aparecem claramente; somente conteúdos publicados são exibidos. |
| **P1** | Imagens múltiplas e associação aos passos | Ordem e legendas persistem e aparecem corretamente. |
| **P1** | Filtros por categoria e tags | O catálogo pode ser encontrado sem depender de termos livres inconsistentes. |
| **P1** | Histórico básico de alterações e revisão | Alterações relevantes indicam autor, data e conteúdo anterior. |
| **Posterior** | Biblioteca de cuidados reutilizáveis, vídeo, recomendação individual e seleção automática por condição clínica | Exigem regras de negócio e validação profissional adicionais. |

### Resultado esperado para a primeira versão

O módulo estará pronto para o piloto quando um profissional conseguir **criar, revisar e publicar uma orientação completa**, o participante conseguir **entender sua execução e seus cuidados**, e o sistema conseguir **preservar as realizações anteriores mesmo após a edição ou inativação do conteúdo**. Esse é um critério mais útil para orientar a refatoração do que apenas contar quantos campos foram adicionados.

---

## Implementação e checklist — 12/10/2026

### Regras adotadas nesta implementação

- **Permissões:** administradores e profissionais de saúde podem criar, editar, revisar, publicar e inativar, conforme definição do responsável pelo projeto durante a implementação. A identidade do revisor vem da autenticação, não de um ID enviado pelo formulário.
- **Publicação:** exige nome, resumo, categoria ativa, ao menos um passo preenchido, os três grupos de cuidados preenchidos, capa e confirmação explícita da revisão profissional. Não há preenchimento automático de conteúdo clínico nem conclusão de ausência de contraindicações a partir de campos vazios.
- **Rascunhos:** aceitam conteúdo incompleto. Um cadastro sem nome recebe “Orientação sem título”, que deve ser substituído antes de publicar. Alterar texto, passos, cuidados ou mídia devolve a orientação a rascunho e invalida a revisão anterior.
- **Histórico:** alterações, publicações, inativações e mudanças de mídia preservam uma fotografia do conteúdo anterior, autor e data. Arquivos removidos do conteúdo atual são retidos no armazenamento para manter esse histórico. Os uploads novos tentam ser removidos do armazenamento se a transação falhar.
- **Imagens:** até 10 por orientação; PNG/JPEG de até 5 MB e 20 megapixels, com verificação do conteúdo do arquivo. Ordem, capa, legenda, texto alternativo e vínculo com um passo são persistidos. O acesso às imagens mantém o serviço R2 já utilizado pelo projeto.
- **Categorias iniciais:** Equilíbrio, Mobilidade e Fortalecimento. São opções de organização para validação da equipe, não recomendações clínicas. Categorias e tags podem ser gerenciadas fora do editor; não há criação de tags livres dentro da orientação.
- **Compatibilidade:** mantém os caminhos `/api/orientacoes-funcionais` e `/api/tags-funcionais`, o campo `descricao` como espelho do resumo nos novos salvamentos e `ativo` como indicador de publicação. A antiga ativação direta é rejeitada: deve-se usar o fluxo de revisão/publicação. A operação antiga de exclusão de orientação passa a inativar, preservando os registros.
- **Migração V19:** preserva os IDs, vínculos com tags e realizações; copia a descrição para o resumo, importa a imagem como capa e torna os conteúdos anteriores rascunhos. Não transforma automaticamente a descrição em instruções. Requer execução pelo Flyway no ambiente de destino.

### Backend

- [x] Modelar status `RASCUNHO`, `PUBLICADA` e `INATIVA`, categoria, resumo, duração, materiais e dados de revisão.
- [x] Persistir passos e cuidados ordenados em tabelas próprias; manter IDs de passos ao editar e reordenar.
- [x] Manter a entidade de realização separada do catálogo e do cálculo de adesão alimentar.
- [x] Criar a migração V19 preservando orientações, imagens, tags e realizações existentes.
- [x] Salvar rascunhos incompletos e validar o conteúdo antes da publicação.
- [x] Restringir catálogo e consulta por ID do participante às orientações publicadas.
- [x] Impedir novas realizações de orientações não publicadas; preservar consulta/alteração das realizações anteriores conforme suas regras existentes.
- [x] Restringir registro, alteração e histórico do próprio participante pela identidade autenticada; manter consulta de histórico por profissionais autorizados.
- [x] Implementar publicação, inativação e histórico com autor e data.
- [x] Implementar upload, edição de metadados, exclusão do vínculo, ordenação, escolha de capa e associação da mídia ao passo.
- [x] Validar quantidade, tipo, tamanho e dimensões das imagens; rejeitar associação a passo de outra orientação.
- [x] Implementar filtros paginados por categoria e tag.
- [x] Disponibilizar gestão de categorias e inativação de tags sem apagar vínculos existentes.

### Frontend

- [x] Refatorar o editor nas quatro etapas das referências: Informações, Execução, Cuidados e Revisão.
- [x] Usar `illustrations-balancing.png` nas quatro etapas, com as cores, tipografia e componentes existentes.
- [x] Estender os painéis do editor até a parte inferior da área útil no desktop e permitir rolagem interna quando o conteúdo exceder sua altura.
- [x] Selecionar categorias e tags cadastradas, sem criar tags livres no formulário.
- [x] Adicionar, remover e reordenar passos e itens dos três grupos de cuidados.
- [x] Adicionar, substituir e remover imagens; escolher capa, ordenar, editar legenda/texto alternativo e associar a passos.
- [x] Salvar rascunho em qualquer etapa e permitir continuar editando; preservar o ID do cadastro e os uploads já concluídos para nova tentativa após falha parcial.
- [x] Apresentar prévia compartilhada com a página de leitura do participante.
- [x] Exibir pendências e exigir confirmação da revisão antes de habilitar publicação.
- [x] Atualizar cartões com resumo, categoria, capa e status interno; incluir filtros por categoria e tag.
- [x] Criar leitura detalhada com finalidade, materiais, duração, passos, imagens e cuidados antes do registro voluntário.
- [x] Integrar o registro voluntário de realização à API existente para o participante autenticado.
- [x] Disponibilizar histórico de alterações no editor com visualização do conteúdo anterior.
- [x] Alinhar a navegação principal para “Orientações funcionais” e “Categorias e tags”.
- [x] Criar interface de gestão de categorias junto à gestão de tags; preservar a inativação como operação sem exclusão dos vínculos.

### Verificações e pendências de homologação

- [x] Compilação do backend e testes unitários do fluxo de publicação/edição/inativação, acesso a rascunhos e validação de imagem.
- [x] Teste isolado da migração V19 em H2 no modo PostgreSQL, confirmando IDs, tags, resumo, capa e realizações preservados. Total: 10 testes executados com sucesso (9 de serviço e 1 de migração).
- [x] Compilação de produção do frontend e verificação de tipos TypeScript.
- [x] ESLint dos arquivos do módulo funcional alterados e novos. A verificação ampliada também apontou um erro preexistente em `Sidebar.tsx:109` (`react-hooks/set-state-in-effect`), fora das alterações de nomenclatura/layout desta tarefa.
- [x] Conferência visual local em desktop (1600 px) e celular (390 px), com respostas simuladas: navegação entre etapas, envio de rascunho incompleto, bloqueio da publicação incompleta, painéis até a base da área útil e rolagem interna nas etapas longas; sem erros de JavaScript ou transbordamento horizontal no celular.
- [ ] Executar a migração V19 em uma cópia PostgreSQL do banco e conferir a preservação dos dados reais. O teste isolado utiliza H2 em modo PostgreSQL; isso não substitui a homologação no PostgreSQL.
- [ ] Homologar upload, substituição, retenção e recuperação de imagens no R2 real e definir o processo operacional de limpeza de arquivos órfãos após falhas de armazenamento.
- [ ] Homologar o fluxo completo com API real e usuários dos três perfis (administrador, profissional e participante).
- [ ] Validar 3 a 5 orientações reais, as categorias iniciais e os textos dos cuidados com a equipe responsável. Nenhum conteúdo clínico foi produzido automaticamente.
- [ ] Revisar manualmente os conteúdos antigos migrados para rascunho antes de publicá-los.
- [x] Autorização explícita recebida e alterações versionadas em commits lógicos nos repositórios da API e da interface.
- [ ] Revisão final do responsável pelo projeto após o versionamento.

### Itens posteriores previstos no guia

- [ ] Biblioteca reutilizável de cuidados e materiais.
- [ ] Vídeo e outros formatos de mídia.
- [ ] Recomendação individual ou seleção automática por condição clínica.
- [ ] Sistema completo de versões, além do histórico básico de conteúdo implementado.
