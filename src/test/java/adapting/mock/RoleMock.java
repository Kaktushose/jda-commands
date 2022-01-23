package adapting.mock;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleIcon;
import net.dv8tion.jda.api.managers.RoleManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Collection;
import java.util.EnumSet;

@SuppressWarnings("ConstantConditions")
public class RoleMock implements Role {

    private final String name;
    private final long id;

    public RoleMock(String name, long id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public int getPositionRaw() {
        return 0;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isManaged() {
        return false;
    }

    @Override
    public boolean isHoisted() {
        return false;
    }

    @Override
    public boolean isMentionable() {
        return false;
    }

    @Override
    public long getPermissionsRaw() {
        return 0;
    }

    @Nullable
    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public int getColorRaw() {
        return 0;
    }

    @Override
    public boolean isPublicRole() {
        return false;
    }

    @Override
    public boolean canInteract(@NotNull Role role) {
        return false;
    }

    @NotNull
    @Override
    public Guild getGuild() {
        return null;
    }

    @NotNull
    @Override
    public EnumSet<Permission> getPermissions() {
        return null;
    }

    @NotNull
    @Override
    public EnumSet<Permission> getPermissions(@NotNull GuildChannel guildChannel) {
        return null;
    }

    @NotNull
    @Override
    public EnumSet<Permission> getPermissionsExplicit() {
        return null;
    }

    @NotNull
    @Override
    public EnumSet<Permission> getPermissionsExplicit(@NotNull GuildChannel guildChannel) {
        return null;
    }

    @Override
    public boolean hasPermission(@NotNull Permission... permissions) {
        return false;
    }

    @Override
    public boolean hasPermission(@NotNull Collection<Permission> collection) {
        return false;
    }

    @Override
    public boolean hasPermission(@NotNull GuildChannel guildChannel, @NotNull Permission... permissions) {
        return false;
    }

    @Override
    public boolean hasPermission(@NotNull GuildChannel guildChannel, @NotNull Collection<Permission> collection) {
        return false;
    }

    @Override
    public boolean canSync(@NotNull GuildChannel guildChannel, @NotNull GuildChannel guildChannel1) {
        return false;
    }

    @Override
    public boolean canSync(@NotNull GuildChannel guildChannel) {
        return false;
    }

    @NotNull
    @Override
    public RoleAction createCopy(@NotNull Guild guild) {
        return null;
    }

    @NotNull
    @Override
    public RoleManager getManager() {
        return null;
    }

    @NotNull
    @Override
    public AuditableRestAction<Void> delete() {
        return null;
    }

    @NotNull
    @Override
    public JDA getJDA() {
        return null;
    }

    @NotNull
    @Override
    public RoleTags getTags() {
        return null;
    }

    @Nullable
    @Override
    public RoleIcon getIcon() {
        return null;
    }

    @Override
    public int compareTo(@NotNull Role o) {
        return 0;
    }

    @NotNull
    @Override
    public String getAsMention() {
        return null;
    }

    @Override
    public long getIdLong() {
        return id;
    }
}
