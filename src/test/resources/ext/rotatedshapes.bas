10 GRON 640,480:BUFFERMODE 1
15 A=LOADSHAPE("src/test/resources/bin/sprite.png")
16 B=LOADSHAPE("src/test/resources/bin/sprite2.png")
20 FOR I=0TO480
21 CLEAR
25 ROTATESHAPE A,100,100,1,0
30 ROTATESHAPE B,200,200,I/100,-I/200
32 ROTATESHAPE A,300,200,1,-i/100
35 FLIP
36 rem LIMIT 60
40 NEXTI
50 GROFF