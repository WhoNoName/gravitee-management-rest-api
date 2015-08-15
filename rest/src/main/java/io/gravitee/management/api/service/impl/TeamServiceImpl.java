/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.management.api.service.impl;

import io.gravitee.management.api.exceptions.TechnicalManagementException;
import io.gravitee.management.api.model.NewTeamEntity;
import io.gravitee.management.api.model.TeamEntity;
import io.gravitee.management.api.service.TeamService;
import io.gravitee.repository.api.TeamMembershipRepository;
import io.gravitee.repository.api.TeamRepository;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author David BRASSELY (brasseld at gmail.com)
 */
@Component
public class TeamServiceImpl implements TeamService {

    /**
     * Logger.
     */
    private final Logger LOGGER = LoggerFactory.getLogger(TeamServiceImpl.class);

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMembershipRepository teamMembershipRepository;

    @Override
    public Optional<TeamEntity> findByName(String teamName) {
        try {
            LOGGER.debug("Find team by name: {}", teamName);
            return teamRepository.findByName(teamName).map(TeamServiceImpl::convert);
        } catch (TechnicalException ex) {
            LOGGER.error("An error occurs while trying to find a team using its name {}", teamName, ex);
            throw new TechnicalManagementException("An error occurs while trying to find a team using its name " + teamName, ex);
        }
    }

    @Override
    public TeamEntity create(NewTeamEntity team) {
        TeamEntity t = new TeamEntity();
        return t;
    }

    @Override
    public TeamEntity update(NewTeamEntity team) {
        TeamEntity t = new TeamEntity();
        return t;
    }

    @Override
    public Set<TeamEntity> findByUser(String username) {
        try {
            LOGGER.debug("Find teams for user {}", username);
            Set<Team> teams = teamMembershipRepository.findByUser(username);
            Set<TeamEntity> publicTeams = new HashSet<>(teams.size());

            for(Team team : teams) {
                publicTeams.add(convert(team));
            }

            return publicTeams;
        } catch (TechnicalException ex) {
            LOGGER.error("An error occurs while trying to find teams for user {}", username, ex);
            throw new TechnicalManagementException("An error occurs while trying to find teams for user " + username, ex);
        }
    }

    @Override
    public Set<TeamEntity> findAll(boolean publicOnly) {
        try {
            LOGGER.debug("Find all teams");
            Set<Team> teams = teamRepository.findAll(publicOnly);
            Set<TeamEntity> publicTeams = new HashSet<>(teams.size());

            for(Team team : teams) {
                publicTeams.add(convert(team));
            }

            return publicTeams;
        } catch (TechnicalException ex) {
            LOGGER.error("An error occurs while trying to find all teams", ex);
            throw new TechnicalManagementException("An error occurs while trying to find all teams", ex);
        }
    }

    private static TeamEntity convert(Team team) {
        TeamEntity teamEntity = new TeamEntity();

        teamEntity.setName(team.getName());
        teamEntity.setPrivate(team.isPrivate());
        teamEntity.setDescription(team.getDescription());
        teamEntity.setEmail(team.getEmail());

        teamEntity.setUpdatedAt(team.getUpdatedAt());
        teamEntity.setCreatedAt(team.getCreatedAt());

        return teamEntity;
    }
}