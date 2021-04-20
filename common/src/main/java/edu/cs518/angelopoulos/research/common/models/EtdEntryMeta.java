package edu.cs518.angelopoulos.research.common.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Document(indexName = "etd_entries")
@ToString
public class EtdEntryMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    ///////////////////////////////////////////////////////////

    @Field(type = FieldType.Text)
    @JsonProperty("title")
    private String title;

    @JsonProperty("description_abstract")
    private String descriptionAbstract;

    @Field(type = FieldType.Keyword)
    @JsonProperty("type")
    private String type;

    @JsonProperty("subject")
    private List<String> subject;

    ///////////////////////////////////////////////////////////

    @JsonProperty("contributor_author")
    private String contributorAuthor;

    @JsonProperty("contributor_committeechair")
    private List<String> contributorCommitteeChair;

    @JsonProperty("contributor_committeecochair")
    private List<String> contributorCommitteeCoChair;

    @JsonProperty("contributor_committeemember")
    private List<String> contributorCommitteeMember;

    @Field(type = FieldType.Text)
    @JsonProperty("contributor_department")
    private String contributorDepartment;

    ///////////////////////////////////////////////////////////

    @Field(type = FieldType.Date)
    @JsonProperty("date_accessioned")
    private Date dateAccessioned;

    @Field(type = FieldType.Date)
    @JsonProperty("date_available")
    private Date dateAvailable;

    @Field(type = FieldType.Date)
    @JsonProperty("date_issued")
    private Date dateIssued;

    ///////////////////////////////////////////////////////////

    @Field(type = FieldType.Text)
    @JsonProperty("degree_grantor")
    private String degreeGrantor;

    @Field(type = FieldType.Keyword)
    @JsonProperty("degree_level")
    private String degreeLevel;

    @Field(type = FieldType.Keyword)
    @JsonProperty("degree_name")
    private String degreeName;

    ///////////////////////////////////////////////////////////

    @JsonProperty("identifier_sourceurl")
    private String identifierSourceUrl;

    @JsonProperty("identifier_uri")
    private String identifierUri;

    ///////////////////////////////////////////////////////////

    @Field(type = FieldType.Text)
    @JsonProperty("publisher")
    private String publisher;

    @JsonProperty("rights")
    private String rights;

    ///////////////////////////////////////////////////////////

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonGetter("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonGetter("description_abstract")
    public String getDescriptionAbstract() {
        return descriptionAbstract;
    }

    public void setDescriptionAbstract(String descriptionAbstract) {
        this.descriptionAbstract = descriptionAbstract;
    }

    @JsonGetter("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonGetter("subject")
    public List<String> getSubject() {
        return subject;
    }

    public void setSubject(List<String> subject) {
        this.subject = subject;
    }

    @JsonGetter("contributor_author")
    public String getContributorAuthor() {
        return contributorAuthor;
    }

    public void setContributorAuthor(String contributorAuthor) {
        this.contributorAuthor = contributorAuthor;
    }

    @JsonGetter("contributor_committeechair")
    public List<String>  getContributorCommitteeChair() {
        return contributorCommitteeChair;
    }

    public void setContributorCommitteeChair(List<String>  contributorCommitteeChair) {
        this.contributorCommitteeChair = contributorCommitteeChair;
    }

    @JsonGetter("contributor_committeecochair")
    public List<String>  getContributorCommitteeCoChair() {
        return contributorCommitteeCoChair;
    }

    public void setContributorCommitteeCoChair(List<String> contributorCommitteeCoChair) {
        this.contributorCommitteeCoChair = contributorCommitteeCoChair;
    }

    @JsonGetter("contributor_committeemember")
    public List<String> getContributorCommitteeMember() {
        return contributorCommitteeMember;
    }

    public void setContributorCommitteeMember(List<String> contributorCommitteeMember) {
        this.contributorCommitteeMember = contributorCommitteeMember;
    }

    @JsonGetter("contributor_department")
    public String getContributorDepartment() {
        return contributorDepartment;
    }

    public void setContributorDepartment(String contributorDepartment) {
        this.contributorDepartment = contributorDepartment;
    }

    @JsonGetter("date_accessioned")
    public Date getDateAccessioned() {
        return dateAccessioned;
    }

    public void setDateAccessioned(Date dateAccessioned) {
        this.dateAccessioned = dateAccessioned;
    }

    @JsonGetter("date_available")
    public Date getDateAvailable() {
        return dateAvailable;
    }

    public void setDateAvailable(Date dateAvailable) {
        this.dateAvailable = dateAvailable;
    }

    @JsonGetter("date_issued")
    public Date getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(Date dateIssued) {
        this.dateIssued = dateIssued;
    }

    @JsonGetter("degree_grantor")
    public String getDegreeGrantor() {
        return degreeGrantor;
    }

    public void setDegreeGrantor(String degreeGrantor) {
        this.degreeGrantor = degreeGrantor;
    }

    @JsonGetter("degree_level")
    public String getDegreeLevel() {
        return degreeLevel;
    }

    public void setDegreeLevel(String degreeLevel) {
        this.degreeLevel = degreeLevel;
    }

    @JsonGetter("degree_name")
    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    @JsonGetter("identifier_sourceurl")
    public String getIdentifierSourceUrl() {
        return identifierSourceUrl;
    }

    public void setIdentifierSourceUrl(String identifierSourceUrl) {
        this.identifierSourceUrl = identifierSourceUrl;
    }

    @JsonGetter("identifier_uri")
    public String getIdentifierUri() {
        return identifierUri;
    }

    public void setIdentifierUri(String identifierUri) {
        this.identifierUri = identifierUri;
    }

    @JsonGetter("publisher")
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @JsonGetter("rights")
    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }
}
