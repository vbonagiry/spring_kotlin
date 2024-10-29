package com.kotlinspring.coursecatalog.service

import com.kotlinspring.coursecatalog.dto.InstructorDTO
import com.kotlinspring.coursecatalog.entity.Instructor
import com.kotlinspring.coursecatalog.repository.InstructorRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class InstructorService(val instructorRepository: InstructorRepository) {

    fun createInstructor(instructorDTO: InstructorDTO): InstructorDTO {
        val instructorEntity = instructorDTO.let {
            Instructor(it.id, it.name)
        }

        instructorRepository.save(instructorEntity)

        return instructorEntity.let {
            InstructorDTO(it.id, it.name)
        }
    }

    fun findByInstructorId(instructorId: Int) : Optional<Instructor> {
        return instructorRepository.findById(instructorId)

    }
}
