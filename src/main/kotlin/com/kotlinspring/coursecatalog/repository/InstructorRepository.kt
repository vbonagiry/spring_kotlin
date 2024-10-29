package com.kotlinspring.coursecatalog.repository

import com.kotlinspring.coursecatalog.entity.Instructor
import org.springframework.data.repository.CrudRepository

interface InstructorRepository : CrudRepository<Instructor, Int> {
}