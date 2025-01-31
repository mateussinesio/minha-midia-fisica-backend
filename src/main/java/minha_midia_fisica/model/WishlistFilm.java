package minha_midia_fisica.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users_wishlist_films")
public class WishlistFilm {
    @Id
    private String id;
    private String userId;
    private Integer filmId;
    private String title;
    private String releaseDate;
    private String originalTitle;
    private String director;
    private String overview;
    private String posterPath;
    private List<String> categories;

    public WishlistFilm(String userId, Integer filmId, String title, String releaseDate, String originalTitle, String director, String overview, String posterPath, List<String> categories) {
        this.userId = userId;
        this.filmId = filmId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.originalTitle = originalTitle;
        this.director = director;
        this.overview = overview;
        this.posterPath = posterPath;
        this.categories = categories;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getFilmId() {
        return filmId;
    }

    public void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}