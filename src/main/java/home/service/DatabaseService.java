package home.service;

import home.entity.Teacher;
import home.repository.TeacherRepository;
import home.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

@Stateless
public class DatabaseService {
    private final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    @PersistenceContext(unitName = "dictionary")
    private EntityManager entityManager;

    @Inject
    private TeacherRepository teacherRepository;

    public void createTeachers() {
        for (int i = 0; i < 1904; i++) {
            Teacher teacher = new Teacher()
                    .setFirstName(Constants.getRandomFirstName())
                    .setLastName(Constants.getRandomLastName())
                    .setMiddleName("T");
            entityManager.persist(teacher);
            logger.debug(teacher + " created.");
        }
    }

    public void createTeachersUsingRepository() {
        for (int i = 0; i < 82; i++) {
            Teacher teacher = new Teacher()
                    .setFirstName(Constants.getRandomFirstName())
                    .setLastName(Constants.getRandomLastName())
                    .setMiddleName("T");
            teacherRepository.save(teacher);
            logger.debug(teacher + " created using repository.");
        }
    }
}
