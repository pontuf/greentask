package com.shilkov.greenatom;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

//import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

@RepositoryRestResource(collectionResourceRel = "files", path = "files")
public interface FileRepository extends PagingAndSortingRepository<FileEntity, Integer> {
	List<FileEntity> findById(@Param("id") int id);
	List<FileEntity> findByFileuser(String fileuser);
	List<FileEntity> findByDate(Date date);
	List<FileEntity> findByName(String name);
	
}
