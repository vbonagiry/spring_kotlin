package com.kotlinspring.coursecatalog.controller

import com.kotlinspring.coursecatalog.dto.CourseDTO
import com.kotlinspring.coursecatalog.service.CourseService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/courses")
@Validated
class CourseController(val courseService: CourseService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addCourse(@RequestBody @Valid courseDto: CourseDTO): CourseDTO {
        return courseService.addCourse(courseDto);
    }

    @GetMapping
    fun retrieveAllCourses(@RequestParam("course_name", required = false) courseName: String?) : List<CourseDTO>
    = courseService.retrieveAllCourses(courseName)

    @PutMapping("/{course_id}")
    fun updateCourse(@RequestBody courseDTO : CourseDTO, @PathVariable("course_id") courseId: Int)
    = courseService.updateCourse(courseId, courseDTO)

    @DeleteMapping("/{course_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCourse(@PathVariable("course_id") courseId: Int)
    = courseService.deleteCourse(courseId)
}