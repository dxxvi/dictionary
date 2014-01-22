package home.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "TEACHER")
public class Teacher {
    @TableGenerator(name = "StudentTeacherIdGenerator", table = "ID_GENERATOR", pkColumnName = "GENERATOR_NAME",
            valueColumnName = "GENERATOR_VALUE", initialValue = 1, allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "StudentTeacherIdGenerator")
    private Long id;
    
    private String firstName;
    private String lastName;
    
    @Column(nullable = false, length = 1)
    private String middleName;

    @Override
    public String toString() {
        return String.format("Teacher {id = %d - %s %s %s}", id, firstName, middleName, lastName);
    }

    public Long getId() {
        return id;
    }

    public Teacher setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public Teacher setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Teacher setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getMiddleName() {
        return middleName;
    }

    public Teacher setMiddleName(String middleName) {
        this.middleName = middleName;
        return this;
    }
}
