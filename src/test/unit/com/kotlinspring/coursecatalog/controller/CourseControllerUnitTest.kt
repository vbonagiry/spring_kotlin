package com.kotlinspring.coursecatalog.controller

import com.kotlinspring.coursecatalog.dto.CourseDTO
import com.kotlinspring.coursecatalog.entity.Course
import com.kotlinspring.coursecatalog.service.CourseService
import com.kotlinspring.coursecatalog.util.courseDTO
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.assertEquals

@WebMvcTest(controllers = [CourseController::class])
@AutoConfigureWebTestClient
class CourseControllerUnitTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    lateinit var courseServiceMockk: CourseService

    @Test
    fun addCourse() {
        val courseDTO = CourseDTO(null, "Kotlin", "Development", 1)

        every { courseServiceMockk.addCourse(any()) } returns courseDTO(id = 1)
        val savedCourseDTO = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertTrue {
            savedCourseDTO!!.id != null
        }
    }

    @Test
    fun addCourse_validation() {
        val courseDTO = CourseDTO(null, "", "", 1)

        every { courseServiceMockk.addCourse(any()) } returns courseDTO(id = 1)
        val response = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        assertEquals("courseDTO.category must not be blank, courseDTO.name must not be blank", response)
    }

    @Test
    fun addCourse_runtimeException() {
        val courseDTO = CourseDTO(null, "Kotlin", "Development", 1)

        val errorMessage = "Unexpected Error occurred"
        every { courseServiceMockk.addCourse(any()) } throws RuntimeException(errorMessage)
        val response = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        assertEquals(errorMessage, response)
    }

    @Test
    fun retrieveAllCourses() {

        every { courseServiceMockk.retrieveAllCourses(any()) } returns (
                listOf(courseDTO(id = 1), courseDTO(id = 2, name = "Microservices"))
        )

        val courseDTOs = webTestClient
            .get()
            .uri("/v1/courses")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody
        println("courseDTOs : $courseDTOs")
        Assertions.assertEquals(2, courseDTOs!!.size)
    }

    @Test
    fun updateCourse() {
        val course = Course(null,
            "Build RestFul APis using SpringBoot and Kotlin", "Development")

        every { courseServiceMockk.updateCourse(any(), any()) } returns CourseDTO(100,
            "Build RestFul APis using SpringBoot and Kotlin1", "Development")

        val updatedCourseDTO = Course(null,
            "Build RestFul APis using SpringBoot and Kotlin1", "Development")

        val updatedCourse = webTestClient
            .put()
            .uri("/v1/courses/{courseId}", 100)
            .bodyValue(updatedCourseDTO)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals("Build RestFul APis using SpringBoot and Kotlin1", updatedCourse!!.name)
    }

    @Test
    fun deleteCourse() {
        val course = Course(null,
            "Build RestFul APis using SpringBoot and Kotlin", "Development")
        every { courseServiceMockk.deleteCourse(any()) } just runs

        webTestClient
            .delete()
            .uri("/v1/courses/{courseId}", 100)
            .exchange()
            .expectStatus().isNoContent
    }
}