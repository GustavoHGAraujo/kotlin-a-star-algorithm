abstract class AlgorithmAStar<V : Graph.Vertex, E : Graph.Edge<V>>(
    protected val edges: List<E>
) : Graph {
    private val V.neighbors: List<V>
        get() = edges
            .asSequence()
            .filter { it.a == this || it.b == this }
            .map { listOf(it.a, it.b) }
            .flatten()
            .filterNot { it == this }
            .distinct()
            .toList()

    private val E.cost: Double
        get() = costToMoveThrough(this)

    private fun findRoute(from: V, to: V): E? {
        return edges.find {
            (it.a == from && it.b == to) || (it.a == to && it.b == from)
        }
    }

    private fun findRouteOrElseCreateIt(from: V, to: V): E {
        return findRoute(from, to) ?: createEdge(from, to)
    }

    private fun generatePath(currentPos: V, cameFrom: Map<V, V>): List<V> {
        val path = mutableListOf(currentPos)
        var current = currentPos

        while (cameFrom.containsKey(current)) {
            current = cameFrom.getValue(current)
            path.add(0, current)
        }
        return path.toList()
    }

    abstract fun costToMoveThrough(edge: E): Double
    abstract fun createEdge(from: V, to: V): E

    fun findPath(begin: V, end: V): Pair<List<V>, Double> {
        val cameFrom = mutableMapOf<V, V>()

        val openVertices = mutableSetOf(begin)
        val closedVertices = mutableSetOf<V>()

        val costFromStart = mutableMapOf(begin to 0.0)

        val estimatedRoute = findRouteOrElseCreateIt(from = begin, to = end)
        val estimatedTotalCost = mutableMapOf(begin to estimatedRoute.cost)

        while (openVertices.isNotEmpty()) {
            val currentPos = openVertices.minBy { estimatedTotalCost.getValue(it) }!!

            // Check if we have reached the finish
            if (currentPos == end) {
                // Backtrack to generate the most efficient path
                val path = generatePath(currentPos, cameFrom)

                // First Route to finish will be optimum route
                return Pair(path, estimatedTotalCost.getValue(end))
            }

            // Mark the current vertex as closed
            openVertices.remove(currentPos)
            closedVertices.add(currentPos)

            (currentPos.neighbors - closedVertices).forEach { neighbour ->
                val routeCost = findRouteOrElseCreateIt(from = currentPos, to = neighbour).cost
                val cost: Double = costFromStart.getValue(currentPos) + routeCost

                if (cost < costFromStart.getOrDefault(neighbour, Double.MAX_VALUE)) {
                    if (!openVertices.contains(neighbour)) {
                        openVertices.add(neighbour)
                    }

                    cameFrom[neighbour] = currentPos
                    costFromStart[neighbour] = cost

                    val estimatedRemainingRouteCost = findRouteOrElseCreateIt(from = neighbour, to = end).cost
                    estimatedTotalCost[neighbour] = cost + estimatedRemainingRouteCost
                }
            }
        }

        throw IllegalArgumentException("No Path from Start $begin to Finish $end")
    }
}