package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.TargettedSkill;
import com.herocraftonline.heroes.util.Setting;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SkillPositionSwap extends TargettedSkill
{
  public SkillPositionSwap(Heroes plugin)
  {
    super(plugin, "PositionSwap");
    setDescription("Swap positions with your target.");
    setUsage("/skill positionswap <target>");
    setArgumentRange(0, 1);
    setIdentifiers(new String[] { 
      "skill positionswap" });

    setTypes(new SkillType[] { 
      SkillType.SILENCABLE, SkillType.DAMAGING, SkillType.HARMFUL, SkillType.TELEPORT });
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set(Setting.DAMAGE.node(), Integer.valueOf(4));
    return node;
  }

  public SkillResult use(Hero hero, LivingEntity target, String[] args)
  {
    Player player = hero.getPlayer();
    Location tlocation = target.getLocation();
    Location plocation = player.getLocation();
    player.teleport(tlocation);
    target.teleport(plocation);
    broadcastExecuteText(hero, target);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    int damage = SkillConfigManager.getUseSetting(hero, this, Setting.DAMAGE, 4, false);
    return getDescription().replace("$1", damage + "");
  }
}

/* Location:           C:\Users\Andrew\Desktop\K1ll3z\bin\decomp.jar
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillPositionSwap
 * JD-Core Version:    0.6.2
 */