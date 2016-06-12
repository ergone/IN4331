/*
 * The MIT License
 *
 * Copyright 2016 Piotr Tekieli <p.s.tekieli@student.tudelft.nl>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.tudelft.wdm.imdb.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import java.util.ArrayList;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Piotr Tekieli <p.s.tekieli@student.tudelft.nl>
 * @version v0.1 (15.05.2016)
 * @version v0.2 (18.05.2016)
 * @version v0.3s (19.05.2016)
 * @version v0.4 (28.05.2016)
 * 
 **/
@JsonSerialize(include = Inclusion.NON_NULL) /* Omit empty ArrayLists */
//To prevent the getters being used in the JSON output
@JsonIgnoreProperties({"id"})
@XmlRootElement (name = "Genre")
public class Genre {
    @XmlElement (name = "Genre ID")
    private final Long idGenre;
    @XmlElement (name = "Name")
    private final String Label;    
    
    @XmlElement (name = "Movies")
    private ArrayList<Movie> Movies;
    @XmlElement (name = "References")
    private ArrayList<Link> References;
    @XmlElement (name = "Number Of Movies")
    private Integer Statistic = null;
    
    public Genre(Long idGenre, String Label) {
        this.Label = Label;
        this.idGenre = idGenre;
    }
    
    public Long getId(){
        return idGenre;
    }
    
    public void AddReference(Link Link) {
        if (References == null) {
            References = new ArrayList<>();
        }        
        References.add(Link);
    }
    
    public void AddMovie(Movie movie) {
        if (Movies == null)
            Movies = new ArrayList<>();        
        Movies.add(movie);
    }   
    
    public void SetStatistic(Integer Statistic) {
        this.Statistic = Statistic;
    }
    
    public ArrayList<Movie> displayMovies() {
        return Movies;
    }
    
    public Integer displayStatistics() {
        return Statistic;
    }
}
