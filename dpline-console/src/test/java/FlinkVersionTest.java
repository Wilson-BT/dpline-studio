import com.handsome.dao.entity.User;
import com.handsome.common.enums.UserType;
import com.handsome.console.service.impl.FlinkVersionServiceImpl;
import org.junit.Test;

public class FlinkVersionTest {

    @Test
    public void createFlinkVersion(){
        FlinkVersionServiceImpl flinkVersionService = new FlinkVersionServiceImpl();
        User user = new User();
        user.setIsAdmin(UserType.ADMIN_USER.getCode());
//        flinkVersionService.createFlinkVersion(user,"","","/Users/wangchunshun/Documents/IdeaProjects/flink-1.13.2");
    }
}
