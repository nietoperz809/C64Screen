1 s$=chr$(160): l$=chr$(157): u$=chr$(145)
10 print "answer:"s$s$s$s$l$l$l$;: input a$
20 if asc(a$)=160 then print u$;: goto 10
30 print a$
