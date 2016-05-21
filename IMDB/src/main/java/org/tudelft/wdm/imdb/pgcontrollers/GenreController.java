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
package org.tudelft.wdm.imdb.pgcontrollers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tudelft.wdm.imdb.models.Genre;
import org.tudelft.wdm.imdb.models.MessageJSON;

/**
 *
 * @author Piotr Tekieli <p.s.tekieli@student.tudelft.nl>
 * @version v0.1 (15.05.2016)
 * @version v0.2 (18.05.2016)
 * @version v0.3s (19.05.2016)
 * 
 **/
public class GenreController {
    private final ArrayList<String> Queries = new ArrayList<>();
    private final JDBC JDBC = new JDBC();
    private final MessageJSON MessageJSON = new MessageJSON();
    
    public void SetActiveFiltersForCollection(String sort) {        
        /* ---------------------SET DEFAULTS IF NULL------------------------ */                
        if (sort == null)
            sort = "idgenres";
        /* ----------------------------------------------------------------- */
        
        Queries.add("SELECT DISTINCT g.idgenres, g.genre "
            + "FROM genres g "            
            + "ORDER BY g." + sort + ";");                
    }     
    public void SetActiveFiltersForSingle (Long limit, Long offset, Long id, Integer year, Integer endyear, String sort, String sort2) {
        /* ---------------------SET DEFAULTS IF NULL------------------------ */
        if (limit == null)
            limit = (long)100;
        if (offset == null)
            offset = (long)0;
        if (sort == null)
            sort = "idmovies";
        if (sort2 == null)
            sort2 = "title";
        if (year == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());            
            year = cal.get(Calendar.YEAR);
        }
        if (endyear == null)
            endyear = year;
        /* ----------------------------------------------------------------- */    
        Queries.add("SELECT DISTINCT g.idgenres, g.genre "
            + "FROM genres g "            
            + "WHERE g.idgenres = " + id + ";"); /* Q0 */
        Queries.add("SELECT DISTINCT m.idmovies, m.title, m.year " +
            "FROM movies m " +
            "JOIN movies_genres mg " +
            "ON mg.idmovies = m.idmovies " +
            "JOIN genres g " +
            "ON mg.idgenres = g.idgenres " +
            "WHERE g.idgenres = " + id + " " +
            "AND m.year >= " + year + " AND m.year <= " + endyear + " " +
            "ORDER BY m." + sort +", m." + sort2 + " " +
            "LIMIT " + limit + " OFFSET " + offset + ";");        
        Queries.add("SELECT COUNT(DISTINCT m.title) AS number " +
            "FROM genres g " +
            "JOIN movies_genres mg " +
            "ON mg.idgenres=g.idgenres " +
            "JOIN movies m " +
            "ON mg.idmovies = m.idmovies " +                
            "WHERE g.idgenres = " + id + " " +
            "AND m.year >= " + year + " AND m.year <= " + endyear + ";");
    }
   
    public ArrayList<Genre> GetGenreListFromDB() {
        ArrayList<Genre> TemporaryCollection = new ArrayList<>();        
            try {                
                while (JDBC.getResultSet().next()) {
                    TemporaryCollection.add(new Genre(JDBC.getResultSet().getLong("idgenres"), JDBC.getResultSet().getString("genre")));
                }
            } catch (SQLException ex) {
                Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
            }
        JDBC.CloseConnection();
        return TemporaryCollection;
    }
    public MessageJSON GetAllGenres(String sort) {
        SetActiveFiltersForCollection(sort);
        JDBC.PerformQuery(Queries.get(0));
        ArrayList<Genre> Genres = GetGenreListFromDB();
        for (int i = 0; i < Genres.size(); i++)        
            getMessageJSON().AddGenre(Genres.get(i));
        JDBC.CloseConnection();
        return getMessageJSON();
    }
    public MessageJSON GetShortGenreInformation(Long limit, Long offset, Long id, Integer year, Integer endyear, String sort, String sort2) {               
        SetActiveFiltersForSingle(limit, offset, id, year, endyear, sort, sort2);
        JDBC.PerformQuery(Queries.get(0));
        getMessageJSON().AddGenre(GetGenreListFromDB().get(0));
        JDBC.CloseConnection();
        return getMessageJSON(); /* Get first (and only) position */
    }
    public MessageJSON GetDetailedGenreInformation(Long limit, Long offset, Long id, Integer year, Integer endyear, String sort, String sort2) {        
        GetShortGenreInformation(limit, offset, id, year, endyear, sort, sort2);
        GetMoviesInformation();
        GetGenreStatistics();
        return getMessageJSON();
    }   
    public MessageJSON GetMoviesInformation() {
        JDBC.PerformQuery(Queries.get(1));
        try {
            while (JDBC.getResultSet().next()) {
                getMessageJSON().AddMovie(JDBC.getResultSet().getLong("idmovies"), JDBC.getResultSet().getString("title"), JDBC.getResultSet().getInt("year"));    
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        }
        JDBC.CloseConnection(); 
        return getMessageJSON();
    }
    public MessageJSON GetGenreStatistics() {        
        JDBC.PerformQuery(Queries.get(2));
        try {
            while (JDBC.getResultSet().next()) {
                getMessageJSON().SetStatistic(JDBC.getResultSet().getInt("number"));    
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        }
        JDBC.CloseConnection(); 
        return getMessageJSON();
    }
    
     /**
     * @return the MessageJSON
     */
    public MessageJSON getMessageJSON() {
        return MessageJSON;
    }
}