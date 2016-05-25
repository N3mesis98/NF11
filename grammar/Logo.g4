grammar Logo;

@header {
    package logoparsing;
}

INT : '0' | [1-9][0-9]* ;
ID : [_a-zA-Z][_a-zA-Z0-9]* ;
WS : [ \t\r\n]+ -> skip ;

programme :
    liste_declarations?
    liste_instructions?
;

liste_declarations :
    (declarationProcedure|declarationFunction)+
;

liste_instructions :
    (instruction)+
;

declarationProcedure :
    'pour' ID '(' liste_params? ')'
        liste_instructions
    'fin'
;

declarationFunction :
    'pour' ID '(' liste_params? ')'
        liste_instructions?
        'rends' exp
    'fin'
;

liste_params :
    (':'ID)+
;

instruction :
    ID '(' (exp)* ')' # procedureCall
    | 'av' exp # av
    | 'td' exp # td
    | 'tg' exp # tg
    | 'lc' # lc
    | 'bc' # bc
    | 've' # ve
    | 're' exp # re
    | 'fpos' exp exp # fpos
    | 'fcc' exp # fcc
    | 'repete' exp '['liste_instructions']' # repete
    | 'si' exp '['liste_instructions']' ('['liste_instructions']')? # si
    | 'tantque' exp '['liste_instructions']' # tantque
    | 'donne' '"' ID exp # donne
;

exp :
    ID '(' (exp)* ')' # functionCall
    | 'hasard' exp # hasard
    | exp ('*'|'/') exp # mult
    | exp ('+'|'-') exp # sum
    | exp ('=='|'>='|'<='|'>'|'<'|'!=') exp # test
    | '!' exp # neg
    | atom # arule
;

atom :
    INT # int
    | ':'ID # id
    | 'loop' # loop
    | '('exp')' # parent
;
