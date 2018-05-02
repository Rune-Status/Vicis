package rs.emulate.scene3d.backend.opengl

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL
import rs.emulate.scene3d.Geometry
import rs.emulate.scene3d.Node
import rs.emulate.scene3d.Scene
import rs.emulate.scene3d.backend.Renderer
import rs.emulate.scene3d.backend.opengl.target.OpenGLRenderTarget

val Node.glState: OpenGLGeometryState
    get() = metadata.computeIfAbsent(OpenGLRenderer.GL_METADATA_KEY) { OpenGLGeometryState() } as OpenGLGeometryState

/**
 * A scene renderer that uses modern OpenGL to render the scene.
 *
 * @param scene The scene to render.
 * @param target The render target to blit the scene to.
 */
class OpenGLRenderer(scene: Scene, val target: OpenGLRenderTarget, val visible: Boolean = false) : Renderer(scene) {

    /**
     * Address of the GL context backing the renderer.
     */
    var windowContext: Long = NULL

    /**
     * Has a GLFW display been initialized?
     */
    override var initialized: Boolean = false

    /**
     * Create a GLFW backed window and a new OpenGL context.
     */
    //@todo - move into OpenGLRenderTarget
    override fun initialize() {
        if (!glfwInit()) {
            throw RuntimeException("Unable to initialize GLFW")
        }

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
        glfwWindowHint(GLFW_VISIBLE, if (visible) GLFW_TRUE else GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE) //@todo - control this from the render target

        windowContext = glfwCreateWindow(scene.width, scene.height, "offscreen buffer", NULL, NULL)

        if (windowContext == NULL) {
            glfwTerminate()
            throw RuntimeException("Window == null")
        }

        // Bind an OpenGL context for the windowContext to the current thread.
        glfwMakeContextCurrent(windowContext)
        createCapabilities()

        initialized = true
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun render() {
        target.bind()
        scene.update()

        glViewport(0, 0, scene.width, scene.height)
        glClearColor(0f, 0.0f, 0f, 0f)
        glClear(GL_COLOR_BUFFER_BIT)

        val geometryNodes = scene.discover { it is Geometry }
        for (node in geometryNodes) {
            val geometry = node as Geometry
            val glState = geometry.glState

            if (geometry.dirty && !geometry.lock.isLocked) {
                if (!glState.initialized) {
                    glState.initialize(geometry)
                } else {
                    glState.update(geometry)
                }
            } else {
                glState.draw(scene.camera, geometry)
            }
        }

        target.blit()
        glfwSwapBuffers(windowContext)
    }

    override fun stop() {
        // @todo - find all `Geometry` nodes and deallocate their resources
    }

    companion object {
        const val GL_METADATA_KEY = "OpenGLState"
    }
}
