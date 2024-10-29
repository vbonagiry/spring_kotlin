package com.kotlinspring.coursecatalog.service

import com.kotlinspring.coursecatalog.dto.CourseDTO
import com.kotlinspring.coursecatalog.entity.Course
import com.kotlinspring.coursecatalog.exception.CourseNotFoundException
import com.kotlinspring.coursecatalog.exception.InstructorNotValidException
import com.kotlinspring.coursecatalog.repository.CourseRepository
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class CourseService(
    val courseRepository: CourseRepository,
    val instructorService: InstructorService) {

    companion object : KLogging()

    fun addCourse(courseDTO: CourseDTO): CourseDTO {
        val instructorOp =  instructorService.findByInstructorId(courseDTO.instructorId!!)
        if(!instructorOp.isPresent) {
            throw  InstructorNotValidException("Instructor Not valid for the Id : ${courseDTO.instructorId}")
        }
        val courseEntity = courseDTO.let {
            Course(null, it.name, it.category, instructorOp.get())
        }
        courseRepository.save(courseEntity);
        logger.info ( "Saved course is : $courseEntity" )
        return courseEntity.let {
            CourseDTO(it.id, it.name, it.category, it.instructor!!.id)
        }
    }

    fun retrieveAllCourses(courseName: String?): List<CourseDTO> {

        val courses  = courseName?.let {
            courseRepository.findCoursesByName(courseName)
        } ?: courseRepository.findAll()

        return courses
            .map {
                CourseDTO(it.id, it.name, it.category)
            }
    }

    fun updateCourse(courseId: Int, courseDTO: CourseDTO): CourseDTO {
        val existingCourse =  courseRepository.findById(courseId)
        return if(existingCourse.isPresent) {
            existingCourse.get()
                .let {
                    it.name = courseDTO.name
                    it.category = courseDTO.category
                    courseRepository.save(it)
                    CourseDTO(it.id, it.name, it.category)
                }
        } else {
            throw CourseNotFoundException("no course found for id : $courseId")
        }
    }

    fun deleteCourse(courseId: Int): Unit {
        val existingCourse =  courseRepository.findById(courseId)
        return if(existingCourse.isPresent) {
            existingCourse.get()
                .let {
                    courseRepository.deleteById(courseId)
                }
        } else {
            throw CourseNotFoundException("no course found for id : $courseId")
        }
    }
}
