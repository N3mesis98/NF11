grammar Logo; 

@header {
  package logoparsing;
}

INT : '0' | [1-9][0-9]* ;
WS : [ \t\r\n]+ -> skip ;

programme : liste_instructions 
;
liste_instructions :
  (instruction)+   
;

exp :
    exp ('*'|'/') exp # mult
  | exp ('+'|'-') exp # sum
  | atom
;

atom :
    INT
  | '('exp')'
;

instruction :
    'av' exp # av
  | 'td' exp # td
  | 'tg' exp # tg
  | 'lc' # lc
  | 'bc' # bc
  | 've' # ve
  | 're' exp # re
  | 'fpos' exp exp # fpos
  | 'fcc' exp # fcc
;  
   