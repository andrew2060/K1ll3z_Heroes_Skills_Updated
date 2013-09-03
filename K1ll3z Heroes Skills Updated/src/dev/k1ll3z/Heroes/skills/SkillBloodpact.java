package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.HeroRegainHealthEvent;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.characters.skill.TargettedSkill;
import com.herocraftonline.heroes.util.Messaging;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class SkillBloodpact extends TargettedSkill
{
  public SkillBloodpact(Heroes plugin)
  {
    super(plugin, "Bloodpact");
    setDescription("You sacrifice $1 health to heal your target");
    setUsage("/skill bloodpact");
    setArgumentRange(0, 0);
    setIdentifiers(new String[] { "skill bloodpact" });
    setTypes(new SkillType[] { SkillType.ITEM });
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set("hpminus", Integer.valueOf(15));
    node.set("hpplus", Integer.valueOf(20));
    return node;
  }

  public SkillResult use(Hero hero, LivingEntity target, String[] args)
  {
    Player player = hero.getPlayer();
    if (!(target instanceof Player))
      return SkillResult.INVALID_TARGET;
    Hero targetHero = this.plugin.getCharacterManager().getHero((Player)target);
    int hpPlus = SkillConfigManager.getUseSetting(hero, this, "hpplus", 20, false);
    double targetHealth = targetHero.getHealth();
    if (targetHealth >= targetHero.getMaxHealth())
    {
      Messaging.send(player, "Target is already fully healed.", new Object[0]);
      return SkillResult.INVALID_TARGET_NO_MSG;
    }
    HeroRegainHealthEvent hrhEvent = new HeroRegainHealthEvent(targetHero, hpPlus, this);
    this.plugin.getServer().getPluginManager().callEvent(hrhEvent);
    if (hrhEvent.isCancelled())
    {
      Messaging.send(player, "Unable to heal the target at this time!", new Object[0]);
      return SkillResult.CANCELLED;
    }
    targetHero.setHealth((int)(targetHealth + hrhEvent.getAmount()));
    targetHero.syncHealth();
    int hpminus = SkillConfigManager.getUseSetting(hero, this, "hpminus", 15, false);
    hero.setHealth(hero.getHealth() - hpminus);
    hero.syncHealth();
    broadcastExecuteText(hero);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    int hpminus = SkillConfigManager.getUseSetting(hero, this, "hpminus", 15, false);
    return getDescription().replace("$1", hpminus);
  }
}

/* Location:           C:\Users\Andrew\Desktop\K1ll3z\bin\decomp.jar
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillBloodpact
 * JD-Core Version:    0.6.2
 */