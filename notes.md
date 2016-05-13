# NF11 - TP5

*Attention*, `pour` n'est pas à considérer comme une instruction. Sinon, on pourrait déclarer une procédure en cours d'éxecution, dans un `répéte`ou bien même encore dans une autre `procédure`... à proscrire.
On préférera spécifier dans `Logo.g4` un programme comme étant :
1. Une liste optionnelle de déclarations
2. Une liste d'instructions

## La classe procédure
| Procédure |
| --------- |
| nom: String |
| params: String[] |
| instructions: ParseTree |

On garde une référence vers un `ParseTree`, qui est classe mère d'un noeud de Contexte (ici d'une ListeInstructionContext).

Petit refactoring sûrement nécessaire pour l'empilement des variables : à l'appel d'une procédure, empiler la table de ses variables locales...