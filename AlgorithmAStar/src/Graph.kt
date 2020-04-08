interface Graph {
    interface Vertex
    interface Edge<T : Vertex> {
        val a: T
        val b: T
    }
}