package rs.emulate.scene3d.example

import glm_.Vector3f.Vector3f
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import rs.emulate.scene3d.Mesh
import rs.emulate.scene3d.Scene
import rs.emulate.scene3d.backend.opengl.OpenGLRenderer
import rs.emulate.scene3d.backend.opengl.target.OpenGLDefaultRenderTarget

fun main(args: Array<String>) {
    val scene = Scene()
    val triangle = Mesh()

    scene.width = 800
    scene.height = 600
    scene.camera.move(0f, 0f, -5f)
    scene.camera.target.put(Vector3f(0f, 1f, 0f))
    scene.camera.perspective(800, 600, 45f, 0.01f, 100f)
    scene.addChild(triangle)

    triangle.positions = listOf(
        Vector3f(1f, 0f, 0f),
        Vector3f(0f, 2f, 8f),
        Vector3f(-1f, 0f, 0f)
    )

    //@todo - needs a new render target
}
