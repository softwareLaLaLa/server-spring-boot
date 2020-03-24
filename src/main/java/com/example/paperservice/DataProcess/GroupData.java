package com.example.paperservice.DataProcess;

import java.util.List;

public class GroupData {

    private List<List<Integer>> papers; // group������paper
    private List<List<Float>> tags; // group������tag��ض�
    private List<List<Integer>> tagsInGroup; // ÿ��tag���ڵ�group

    public GroupData(List<List<Integer>> papers, List<List<Float>> tags,
                     List<List<Integer>> tagsInGroup) {
        super();
        this.papers = papers;
        this.tags = tags;
        this.tagsInGroup = tagsInGroup;
    }

    public List<List<Integer>> getTagsInGroup() {
        return tagsInGroup;
    }

    public List<List<Integer>> getPapers() {
        return papers;
    }

    public List<List<Float>> getTags() {
        return tags;
    }
}