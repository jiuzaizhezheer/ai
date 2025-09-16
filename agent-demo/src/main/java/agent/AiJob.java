package agent;

import java.util.Map;

public class AiJob {
     public record Job(JobType jobType, Map<String,String> keyInfos) {
    }

    public enum JobType{
        CANCEL,
        QUERY,
        OTHER,
    }
}
