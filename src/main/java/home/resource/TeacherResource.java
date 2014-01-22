package home.resource;

import home.service.DatabaseService;
import home.util.Constants;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/teacher")
public class TeacherResource {
    DateTimeFormatter dtf = DateTimeFormat.forPattern(Constants.dateFormat);

    @Inject
    private DatabaseService databaseService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public String[] createTeachers() {
/*
        databaseService.createTeachers();
        return new String[] {"Some teachers are created at", dtf.print(new DateTime())};
*/
        databaseService.createTeachersUsingRepository();
        return new String[] {"Some teachers are created using repository at", dtf.print(new DateTime())};
    }
}
