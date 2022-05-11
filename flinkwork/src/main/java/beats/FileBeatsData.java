package beats;


import com.datastax.driver.mapping.annotations.Table;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


@Table(keyspace = "example", name = "wordcount")
public class FileBeatsData implements Serializable {
    public Service service;
    public Input input;
    public Agent agent;
    public ArrayList<String> tags;
    public Ecs ecs;
    public String message;
    public Log log;
    @JsonProperty("@timestamp")
    public Date timestamp;
    @JsonProperty("@version")
    public String version;
    public Host host;
    public Event event;
    public Fileset fileset;
}

