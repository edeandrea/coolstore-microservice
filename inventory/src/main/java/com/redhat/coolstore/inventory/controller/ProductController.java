package com.redhat.coolstore.inventory.controller;

import java.util.Arrays;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

import com.redhat.coolstore.inventory.model.Product;
import com.redhat.coolstore.inventory.service.IProductService;


@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductController {
	@Inject
	IProductService productService;

	@GET
	@Path("/{id}")
	public Product getById(@PathParam("id") Long id) {
		Log.debug("Entering ProductController.getById()");
		var p = productService.findById(id);

		if (p == null) {
			Log.error("Product not found");
		}

		return p;
	}

	@GET
	public Response findAll(@QueryParam("sort") String sortString,
		@QueryParam("page") @DefaultValue("0") int pageIndex,
		@QueryParam("size") @DefaultValue("20") int pageSize) {
		Page page = Page.of(pageIndex, pageSize);
		Sort sort = getSortFromQuery(sortString);
		return Response.ok(productService.findAll(page, sort)).build();
	}

	/**
	 * This method tries to mimic the behavior of Spring MVC's @EnableSpringDataWebSupport annotation when it comes to the sort parameter.
	 * @param sortString The string containing the sort query to be used. Must have the "field,asc/desc" format or the second part of the query will be ignored.
	 * @return The {@link Sort} object with the sort criteria to apply.
	 */
	private Sort getSortFromQuery(String sortString) {
		if (sortString != null && !sortString.equals("")) {
			List<String> sortQuery = Arrays.asList(sortString.split(","));
			if (sortQuery == null || sortQuery.size()== 0 || sortQuery.size() >2) {
				return null;
			}
			else {
				if (sortQuery.size() == 1) {
					return Sort.by(sortQuery.get(0));
				} else {
					if (sortQuery.get(1).equals("asc")) {
						return Sort.ascending(sortQuery.get(0));
					} else {
						if (sortQuery.get(1).equals("desc")) {
							return Sort.descending(sortQuery.get(0));
						} else {
							return Sort.by(sortQuery.get(0));
						}
					}
				}
			}
		}
		return null;
	}

}
