import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.LongSerializer
import org.apache.kafka.common.serialization.StringSerializer
import kotlin.math.max
import kotlin.math.min

data class Rating(val movieId: Int, val rating: Double, val ratingTime: java.util.Date)

class JSONRatingStreamer {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val ratingTargets = listOf(
                mapOf("id" to 128, "rating" to 7.9),  // The Big Lebowski
                mapOf("id" to 211, "rating" to 7.7),  // A Beautiful Mind
                mapOf("id" to 552, "rating" to 4.5),  // The Village
                mapOf("id" to 907, "rating" to 7.4),  // True Grit
                mapOf("id" to 354, "rating" to 10.0), // The Tree of Life
                mapOf("id" to 782, "rating" to 8.2),  // A Walk in the Clouds
                mapOf("id" to 802, "rating" to 7.1),  // Gravity
                mapOf("id" to 900, "rating" to 6.5),  // Children of Men
                mapOf("id" to 25, "rating" to 8.9),   // The Goonies
                mapOf("id" to 294, "rating" to 9.1),  // Die Hard
                mapOf("id" to 362, "rating" to 7.8),  // Lethal Weapon
                mapOf("id" to 592, "rating" to 3.4),  // Happy Feet
                mapOf("id" to 744, "rating" to 8.6),  // The Godfather
                mapOf("id" to 780, "rating" to 1.2),  // Super Mario Brothers
                mapOf("id" to 805, "rating" to 7.2),  // Highlander
                mapOf("id" to 833, "rating" to 2.5),  // Bolt
                mapOf("id" to 658, "rating" to 4.6),  // Beowulf
                mapOf("id" to 547, "rating" to 2.3),  // American Pie 2
                mapOf("id" to 496, "rating" to 6.9)   // 13 Going on 30
            )
            val stddev = 2.0

            val props = Properties()
            println("Streaming ratings to ${args[0]}")
            props["bootstrap.servers"] = args[0]
            props["key.serializer"] = LongSerializer::class.java.name
            props["value.serializer"] = StringSerializer::class.java.name

            val producer = KafkaProducer<Long, String>(props)

            try {
                var currentTime = System.currentTimeMillis() / 1000
                println(currentTime)
                var recordsProduced = 0L
                val random = Random()
                while (true) {
                    val numberOfTargets = ratingTargets.size
                    val targetIndex = random.nextInt(numberOfTargets)
                    val randomRating = (random.nextGaussian() * stddev) + ratingTargets[targetIndex]["rating"] as Double
                    val boundedRating = max(0.0, min(randomRating, 10.0))
                    val rating = Rating(ratingTargets[targetIndex]["id"] as Int, boundedRating)

                    if (System.currentTimeMillis() / 1000 > currentTime) {
                        currentTime = System.currentTimeMillis() / 1000
                        println("RATINGS PRODUCED $recordsProduced")
                    }

                    val pr = ProducerRecord("ratings", 
                                            rating.movieId.toLong(), 
                                            Parser.toJson(rating).toString(),
                                            Clock.now().toEpochMilliseconds())
                    producer.send(pr)
                    recordsProduced++
                }
            } finally {
                producer.close()
            }
        }
    }
}
