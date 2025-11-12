package com.ecoMarket.ServicioUsuarios.Assemblers;

import com.ecoMarket.ServicioUsuarios.Controller.UsuarioController;
import com.ecoMarket.ServicioUsuarios.Model.Usuario;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {

    @Override
    public EntityModel<Usuario> toModel(Usuario usuario) {
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).obtenerPorUsername(usuario.getName())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).listar()).withRel("usuarios"));
    }
}
