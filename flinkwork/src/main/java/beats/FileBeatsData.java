package beats;


import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


class Agent{
    public String type;
    public String ephemeral_id;
    public String name;
    public String id;
    public String version;
}

 class Ecs{
    public String version;
}

 class Event{
    public String dataset;
    public String module;
    public String timezone;
    public String original;
}

 class File{
    public String path;
}

 class Fileset{
    public String name;
}

 class Host{
    public boolean containerized;
    public Os os;
    public ArrayList<String> ip;
    public String hostname;
    public String name;
    public String architecture;
    public ArrayList<String> mac;
    public String id;
}

 class Input{
    public String type;
}

 class Log{
    public File file;
    public int offset;
}

 class Os{
    public String codename;
    public String kernel;
    public String type;
    public String platform;
    public String name;
    public String family;
    public String version;
}

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

class Service{
    public String type;
}

