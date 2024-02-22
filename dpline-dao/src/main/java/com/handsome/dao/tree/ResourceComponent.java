package com.handsome.dao.tree;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.handsome.common.enums.ResourceType;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"id","pid","name","fullName","description","isDirctory","children","type"})
public class ResourceComponent {
    public ResourceComponent() {
    }

    public ResourceComponent(int id, int pid, String name, String fullName, String description, boolean isDirctory) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.fullName = fullName;
        this.description = description;
        this.isDirctory = isDirctory;
        int directoryFlag = isDirctory ? 1:0;
        this.idValue = String.format("%s_%s",id,directoryFlag);
    }
    /**
     * id
     */
    protected int id;
    /**
     * parent id
     */
    protected int pid;
    /**
     * name
     */
    protected String name;
    /**
     * current directory
     */
    protected String currentDir;
    /**
     * full name
     */
    protected String fullName;
    /**
     * description
     */
    protected String description;
    /**
     * is directory
     */
    protected boolean isDirctory;
    /**
     * id value
     */
    protected String idValue;
    /**
     * resoruce type
     */
    protected ResourceType type;
    /**
     * children
     */
    protected List<ResourceComponent> children = new ArrayList<>();

    public void add(ResourceComponent resourceComponent){
        children.add(resourceComponent);
    }

    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDirctory() {
        return isDirctory;
    }

    public void setDirctory(boolean dirctory) {
        isDirctory = dirctory;
    }

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(int id,boolean isDirctory) {
        int directoryFlag = isDirctory ? 1:0;
        this.idValue = String.format("%s_%s",id,directoryFlag);
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public List<ResourceComponent> getChildren() {
        return children;
    }

    public void setChildren(List<ResourceComponent> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "ResourceComponent{" +
                "id=" + id +
                ", pid=" + pid +
                ", name='" + name + '\'' +
                ", currentDir='" + currentDir + '\'' +
                ", fullName='" + fullName + '\'' +
                ", description='" + description + '\'' +
                ", isDirctory=" + isDirctory +
                ", idValue='" + idValue + '\'' +
                ", type=" + type +
                ", children=" + children +
                '}';
    }
}
