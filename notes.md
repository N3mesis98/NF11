# NF11 - TP3

## TODO-list
- `exp` et nouveau parcours d'arbre
    + Done
- fonction hasard
    + Done
- fonction repete
    + TBD
- variable loop
    + TBD
- ajouter logs
    + TBD
- rendu Moodle (en fait, non, c'est pas à faire)
    + TBD

## Prise de notes
- On intègre maintenant les expressions arithmétiques.
- Il ne s'agit plus simplement de visiter des noeuds INT, mais bien des noeuds expr

Pour ce faire, on utilise toujours le système de parcours d'arbre avec `visitChildren(ctx)` pour visiter tous les noeuds fils sans distinction, et `visit(ctx.exp())` pour filtrer les noeuds à visiter (ici, les noeuds `exp()`).
De la même manière, chaque noeud peut accéder à la map nommée `atts`

## Rendu Moodle
Question :
>Comment sont interprétés : av hasard 200 + 100 et av 200 + hasard 100 ?
>Pourquoi ?

Cela dépend de la manière dont on a placé la fonction `hasard` dans l'ordre de priorité des opérations arithmétiques de `exp`.
Pour l'instant : hasard est prioritaire (voir l'arbre généré, tout simplement)