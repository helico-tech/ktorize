package nl.helico.website

import io.ktor.server.application.Application
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.article
import kotlinx.html.classes
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.img
import kotlinx.html.li
import kotlinx.html.section
import kotlinx.html.span
import kotlinx.html.title
import kotlinx.html.ul
import nl.helico.ktorize.html.respondHtml

fun Application.routes() {
    routing {
        get("/") {
            call.respondHtml(view = Layout {
                classes += "ht-home"

                section("title") {
                    h1 {
                        span("gradient") {
                            +"H"
                        }
                        span {
                            +"elico"
                        }
                        span("gradient") {
                            +" T"
                        }
                        span {
                            +"ech"
                        }
                    }
                }

                section("subtitle") {
                    h2 {
                        +"Suspendisse vestibulum, orci ac vulputate laoreet, justo massa maximus leo, ut condimentum diam enim in lectus. Sed facilisis at eros nec efficitur. Proin vehicula quam orci, vel maximus dui dictum non. Sed semper leo ac pellentesque eleifend. Cras molestie scelerisque ex, quis auctor eros malesuada nec. "
                    }
                }

                section("services") {
                    Article(
                        title = "Software Development",
                        className = "software-development",
                        imageSrc = "/assets/img/home/development.png",
                        link = "/services/software-development",
                        items = listOf(
                            "Web Development",
                            "Mobile Development",
                            "Desktop Development"
                        )
                    )

                    Article(
                        title = "Lead Developer",
                        className = "lead-developer",
                        imageSrc = "/assets/img/home/developer.png",
                        link = "/services/lead-developer",
                        items = listOf(
                            "Team Lead",
                            "Architect",
                            "Mentor"
                        )
                    )

                    Article(
                        title = "Consultancy",
                        className = "consultancy",
                        imageSrc = "/assets/img/home/consultancy.png",
                        link = "/services/consultancy",
                        items = listOf(
                            "Code Review",
                            "Architecture",
                            "Mentoring"
                        )
                    )
                }
            })
        }
    }
}

internal fun FlowContent.Article(
    title: String,
    className: String,
    imageSrc: String,
    link: String,
    items: List<String>
) {
    article(className) {
        h1 {
            +title
        }

        section {
            img(src = imageSrc, alt = title)
            ul {
                items.forEach {
                    li {
                        +it
                    }
                }
            }
            a(href = link) {
                +"Read more"
            }
        }
    }
}