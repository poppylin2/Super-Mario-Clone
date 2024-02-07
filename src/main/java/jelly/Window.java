package jelly;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;
import jelly.KeyListener;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private int width, height;
    private String title;
    private static Window window = null;
    private long glfwWindow;

    private float r, g, b, a;
    private boolean fadeToblack = false;

    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "Super Mario";
        r = 1;
        b = 1;
        g = 1;
        a = 1;
    }

    public static Window get(){
        if (Window.window == null){
            Window.window = new Window();
        }

        return Window.window;
    }

    public void run(){
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the memory once our loop ends
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init(){
        //  Set up an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW.");
        }
        // Configure glfw
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height,this.title, NULL, NULL);
        if (glfwWindow == NULL){
            throw new IllegalStateException("Failed to create GLFW window.");
        }

        glfwSetCursorPosCallback(glfwWindow, jelly.MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, jelly.MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, jelly.MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        //  Make OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        //  Enable v-sync
        glfwSwapInterval(1);

        //  Make the window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();


    }

    public void loop(){
        float beginTime = Time.getTime();
        float endTime = Time.getTime();

        while (!glfwWindowShouldClose(glfwWindow)){
            //  Poll event
            glfwPollEvents();

            //This function clears the screen using the color specified by the last `glClearColor` call.
            // The `GL_COLOR_BUFFER_BIT` indicates that the color buffer (the part of the framebuffer that holds the image displayed on the screen) should be cleared.
            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if(fadeToblack){
                r = Math.max(r - 0.01f, 0);
                b = Math.max(b - 0.01f, 0);
                g = Math.max(g - 0.01f, 0);
            }
            if(KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
                fadeToblack = true;
            }

            // Displays the currently drawn frame on the screen and starts preparing for the next frame's drawing. Double buffering mechanism, helps prevent screen tearing and flickering
            glfwSwapBuffers(glfwWindow);

            // Determine the frame duration (dt) by finding the time elapsed between endTime and beginTime. Crucial for time-dependent animations and physics to achieve consistent behavior across different frame rates
            endTime = Time.getTime();
            float dt = endTime - beginTime;
            beginTime = endTime;
        }

    }
}
