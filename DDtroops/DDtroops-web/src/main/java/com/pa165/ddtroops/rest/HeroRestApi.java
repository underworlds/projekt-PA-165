/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pa165.ddtroops.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pa165.ddtroops.api.dto.HeroDTO;
import com.pa165.ddtroops.api.dto.RoleDTO;
import com.pa165.ddtroops.api.service.HeroService;
import java.util.HashSet;
import java.util.List;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hibernate.exception.DataException;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * REST Web Service
 *
 * @author Jakub
 */
@Path("/hero")
public class HeroRestApi {

    private static final XmlWebApplicationContext APP_CONF =
            new XmlWebApplicationContext();
    
    @Context
    private ServletContext context;
    private HeroService heroService;
    private ObjectMapper mapper = new ObjectMapper();

    public HeroRestApi() {
        APP_CONF.setNamespace("applicationContext-web");
    }
    
    private void initBeforeRequest() {
        APP_CONF.setServletContext(context);
        APP_CONF.refresh();
        heroService = APP_CONF.getBean(HeroService.class);
        
    }
    
    private void destroyAfterRequest(){
        
    }
    
    private void resetHeroRolesAndTroop(HeroDTO h) {
        h.setRole(new HashSet<RoleDTO>());
        h.setTroop(null);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllHeroes() {
        try{
            initBeforeRequest();
            List<HeroDTO> heroList = heroService.retrieveAllHeroes();
            for(HeroDTO h : heroList) {
                resetHeroRolesAndTroop(h);
            }
            return mapper.writerWithType(new TypeReference<List<HeroDTO>>() {}).writeValueAsString(heroList);
        } catch (DataException ex){
            throw new WebApplicationException(ex, Response.Status.SERVICE_UNAVAILABLE);

        } catch (JsonProcessingException ex) {
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        }catch (Exception ex){
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            destroyAfterRequest();
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public String getHero(@PathParam("id") Long id){
        try{
            initBeforeRequest();
            HeroDTO hero = heroService.retrieveHeroById(id);
            resetHeroRolesAndTroop(hero);
            return mapper.writeValueAsString(hero);
        } catch (DataException ex){
            throw new WebApplicationException(ex, Response.Status.SERVICE_UNAVAILABLE);
        } catch (Exception ex) {
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            destroyAfterRequest();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/post")
    public String postHero(String json){
        try{
            initBeforeRequest();
            HeroDTO hero = mapper.readValue(json, new TypeReference<HeroDTO>(){});
            heroService.createHero(hero);
            return mapper.writeValueAsString(hero);
        } catch (DataException ex){
            throw new WebApplicationException(ex, Response.Status.SERVICE_UNAVAILABLE);
        } catch (JsonMappingException ex){
            throw new WebApplicationException(ex, Response.Status.BAD_REQUEST);
        } catch (Exception ex){
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            destroyAfterRequest();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/put")
    public String putHero(String json){
        try{
            initBeforeRequest();
            HeroDTO hero = mapper.readValue(json, new TypeReference<HeroDTO>(){});
            HeroDTO dataHero = heroService.retrieveHeroById(hero.getId());
            hero.setRole(dataHero.getRole());
            hero.setTroop(dataHero.getTroop());
            heroService.updateHero(hero);
            resetHeroRolesAndTroop(hero);
            return mapper.writeValueAsString(hero);
        } catch (DataException ex){
            throw new WebApplicationException(ex, Response.Status.SERVICE_UNAVAILABLE);
        } catch (JsonMappingException ex){
            throw new WebApplicationException(ex, Response.Status.BAD_REQUEST);
        } catch (Exception ex){
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            destroyAfterRequest();
        }
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/{id}")
    public String deleteHero(@PathParam("id") Long id){
        try{
            initBeforeRequest();
            HeroDTO hero = heroService.retrieveHeroById(id);
            heroService.deleteHero(hero);
            resetHeroRolesAndTroop(hero);
            return mapper.writeValueAsString(hero);
        } catch (DataException ex){
            throw new WebApplicationException(ex, Response.Status.SERVICE_UNAVAILABLE);
        } catch (JsonMappingException ex){
            throw new WebApplicationException(ex, Response.Status.BAD_REQUEST);
        } catch (Exception ex){
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            destroyAfterRequest();
        }
    }
}