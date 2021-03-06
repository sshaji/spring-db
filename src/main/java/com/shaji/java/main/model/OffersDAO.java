package com.shaji.java.main.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("offersDao")
public class OffersDAO {

	private NamedParameterJdbcTemplate jdbc;

	@Autowired
	public void setDataSource(DataSource jdbc) {
		this.jdbc = new NamedParameterJdbcTemplate(jdbc);
	}

	public List<Offer> getOffers() {
		String sql = "select * from offers";

		return jdbc.query(sql, new RowMapper<Offer>() {

			public Offer mapRow(ResultSet rs, int rowNum) throws SQLException {
				Offer offer = new Offer();
				offer.setId(rs.getInt("id"));
				offer.setName(rs.getString("name"));
				offer.setEmail(rs.getString("email"));
				offer.setOfferDetails(rs.getString("offerdetails"));
				return offer;
			}

		});
	}

	public Offer getOfferById(int id) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource("id", id);
		String sql = "select * from offers where id = :id";

		return jdbc.queryForObject(sql, paramMap, new RowMapper<Offer>() {

			public Offer mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new Offer(rs.getInt("id"), rs.getString("name"), rs.getString("email"),
						rs.getString("offerdetails"));
			}

		});
	}

	public int createOffer(Offer offer) {
		BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(offer);
		String sql = "insert into offers (name, email, offerdetails) values (:name, :email, :offerDetails)";

		return jdbc.update(sql, paramSource);
	}

	@Transactional
	public int[] createOffers(List<Offer> offers) {
		SqlParameterSource[] batchParams = SqlParameterSourceUtils.createBatch(offers.toArray());
		String sql = "insert into offers (id, name, email, offerdetails) values (:id, :name, :email, :offerDetails)";

		return jdbc.batchUpdate(sql, batchParams);
	}

	@Transactional
	public int[] updateOffers(List<Offer> offers) {
		SqlParameterSource[] batchParams = SqlParameterSourceUtils.createBatch(offers.toArray());
		String sql = "update offers set name = :name, email = :email, offerdetails = :offerDetails where id = :id";

		return jdbc.batchUpdate(sql, batchParams);
	}

}
