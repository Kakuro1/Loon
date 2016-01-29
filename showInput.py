# -*- coding: utf-8 -*-
"""
Created on Thu Jan 28 13:18:01 2016

@author: David
"""

import numpy as np
from matplotlib.pyplot import cm
from matplotlib import animation
import matplotlib.pyplot as plt
plt.close('all')

filename="loon_r70_c300_a8_radius7_saturation_250.in"
file=open(filename,'r')
bmoves=open("result.out",'r')

rows,cols,alts=[int(s) for s in file.readline().split()]
targets,radius,loons,ticks=[int(s) for s in file.readline().split()]
start_row,start_col=[int(s) for s in file.readline().split()]

radialCells=[]
for delRow in range(-radius,radius+1):
    for delCol in range(-radius,radius+1):
        if(delRow**2+delCol**2<=radius**2 and (delRow!=0 or delCol!=0)):
            radialCells.append((delRow,delCol))

targetGrid=np.ndarray((rows,cols),dtype=int)
targetGrid.fill(0)

targetGrid[start_row,start_col]=2
grid=np.array(targetGrid)
ballonPos=np.ndarray((loons,3),dtype=int)
for i in range(loons):
    ballonPos[i,0]=start_row
    ballonPos[i,1]=start_col
    ballonPos[i,2]=-1
'''
0 ist nix
1 ist bevölkert
2 startzelle nicht bevölkert
3 loons in air
4 loonRadius counting
5 loonRadius not counting
'''

for i in range(targets):
    row,col=[int(s) for s in file.readline().split()]
    targetGrid[row,col]=1

winds=np.ndarray((rows,cols,alts,2),dtype=int)
for alt in range(alts):
    for row in range(rows):
        lineVals=[int(s) for s in file.readline().split()]
        for col in range(cols):
            winds[row,col,alt,0]=lineVals[2*col]
            winds[row,col,alt,1]=lineVals[2*col+1]

file.close()


img=plt.imshow(targetGrid,interpolation='nearest',origin='lower')
plt.show()

ballonMoves=np.ndarray((loons,ticks),dtype=int)
for t in range(ticks):
    ballonMoves[:,t]=[int(s) for s in bmoves.readline().split()]

fig=plt.figure()
im=plt.imshow(targetGrid,interpolation='nearest',origin='lower')


# initialization function: plot the background of each frame
def init():
    im.set_data(targetGrid)
    grid=np.array(targetGrid)
    for i in range(loons):
        ballonPos[i,0]=start_row
        ballonPos[i,1]=start_col
        ballonPos[i,2]=-1
    return [im]

# animation function.  This is called sequentially
def animate(i):
    for k in range(loons):
        if ballonPos[k,0]==-1:
            continue
        ballonPos[k,2]+=ballonMoves[k,i]
        row=ballonPos[k,0]
        col=ballonPos[k,1]
        alt=ballonPos[k,2]
        if alt==-1:
            continue
        ballonPos[k,0]+=winds[row,col,alt,0]
        ballonPos[k,1]+=winds[row,col,alt,1]
        ballonPos[k,1]=(ballonPos[k,1]+cols)%cols
        if ballonPos[k,0]<0 or ballonPos[k,0]>=rows:
            ballonPos[k,0]=-1

    grid=np.array(targetGrid)
    for k in range(loons):
        row=ballonPos[k,0]
        alt=ballonPos[k,2]
        if row==-1 or alt==-1:
            continue
        col=ballonPos[k,1]
        grid[row,col]=3
        for delR,delC in radialCells:
            lr=row+delR
            lc=(col+delC+cols)%cols
            if(lr<0 or lr>=rows):
                continue
            if grid[lr,lc]==0:
                grid[lr,lc]=5
            elif grid[lr,lc]==1:
                grid[lr,lc]=4
    
    im.set_array(grid)
    return [im]

anim = animation.FuncAnimation(fig, animate, init_func=init,
                               frames=ticks, interval=100, blit=False,repeat=False)
plt.show()

'''
plot2=plt.figure()
u = np.array(winds[:,:,0,0],dtype=float)
v = np.array(winds[:,:,0,1],dtype=float)

speed=np.sqrt(u**2+v**2)
lw = 5*speed/speed.max()
plt.streamplot(np.array(range(cols)), np.array(range(rows)), v, u,          # data
               density=3,
               color='DarkRed', 
               linewidth=lw)
plt.axis('equal')
plt.show(plot2)
'''