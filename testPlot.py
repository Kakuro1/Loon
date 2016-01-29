'''
import matplotlib.pyplot as plt
import numpy as np
from matplotlib import animation

# First set up the figure, the axis, and the plot element we want to animate
fig = plt.figure()
ax = plt.axes(xlim=(0, 10), ylim=(0, 10))
#line, = ax.plot([], [], lw=2)
a=np.random.random((5,5))
im=plt.imshow(a,interpolation='none')
# initialization function: plot the background of each frame
def init():
    im.set_data(np.random.random((5,5)))
    return [im]

# animation function.  This is called sequentially
def animate(i):
    a=im.get_array()
    a=a*np.exp(-0.001*i)    # exponential decay of the values
    im.set_array(a)
    return [im]
    
# call the animator.  blit=True means only re-draw the parts that have changed.
anim = animation.FuncAnimation(fig, animate, init_func=init,
                               frames=200, interval=20, blit=True)

# save the animation as an mp4.  This requires ffmpeg or mencoder to be
# installed.  The extra_args ensure that the x264 codec is used, so that
# the video can be embedded in html5.  You may need to adjust this for
# your system: for more information, see
# http://matplotlib.sourceforge.net/api/animation_api.html
anim.save('basic_animation.mp4', fps=30, extra_args=['-vcodec', 'libx264'])

plt.show()
'''
"""
Matplotlib Animation Example

author: Jake Vanderplas
email: vanderplas@astro.washington.edu
website: http://jakevdp.github.com
license: BSD
Please feel free to use and modify this, but keep the above information. Thanks!
"""

import numpy as np
from matplotlib import pyplot as plt
from matplotlib import animation

# First set up the figure, the axis, and the plot element we want to animate
fig = plt.figure()
ax = plt.axes(xlim=(0, 10), ylim=(0, 10))
#line, = ax.plot([], [], lw=2)
a=np.random.random((5,5))
im=plt.imshow(a,interpolation='none')

# initialization function: plot the background of each frame
def init():
    im.set_data(np.random.random((5,5)))
    return [im]

# animation function.  This is called sequentially
def animate(i):
    a=im.get_array()
    a=a*np.exp(-0.001)    # exponential decay of the values
    im.set_array(a)
    return [im]

anim = animation.FuncAnimation(fig, animate, init_func=init,
                               frames=200, interval=20, blit=False,repeat=True)
plt.show()