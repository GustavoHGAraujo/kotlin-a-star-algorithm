# Algorithm A* in Kotlin

This code is based on the implementation available in [Rosseta Code](https://rosettacode.org/wiki/A*_search_algorithm#Kotlin).

This implementation improves legibility and provides a generic class to be used, facilitating the implementation in different contexts.

## Usage example
```kotlin
data class Coordinates(
    val lat: Double,
    val lon: Double
) : Graph.Vertex

data class Route(
    override val a: Coordinates,
    override val b: Coordinates
) : Graph.Edge<Coordinates> {
    val distance: Double
        get() {
            val dLon = abs(a.lon - b.lon)
            val dLat = abs(a.lat - b.lat)
            return sqrt(dLon.pow(2) + dLat.pow(2))
        }
}

class AlgorithmAStarImpl(edges: List<Route>) : AlgorithmAStar<Coordinates, Route>(edges) {
    override fun costToMoveThrough(edge: Route): Double {
        return edge.distance
    }

    override fun createEdge(from: Coordinates, to: Coordinates): Route {
        return Route(from, to)
    }
}

fun main() {
    val routes = listOf(
        Route(Coordinates(1.0, 1.0), Coordinates(1.0, 3.0)),
        Route(Coordinates(1.0, 1.0), Coordinates(5.0, 1.0)),
        Route(Coordinates(5.0, 1.0), Coordinates(2.0, 2.0)),
        Route(Coordinates(1.0, 3.0), Coordinates(2.0, 2.0))
    )

    val result = AlgorithmAStarImpl(routes)
        .findPath(
            begin = Coordinates(1.0, 1.0),
            end = Coordinates(2.0, 2.0)
        )

    val pathString = result.first.joinToString(separator = ", ") { "[${it.lat}, ${it.lon}]" }
    
    println("Result:")
    println("  Path: $pathString")
    println("  Cost: ${result.second}")
}
```

```
# Output
Result:
  Path: [1.0, 1.0], [1.0, 3.0], [2.0, 2.0]
  Cost: 3.414213562373095
```