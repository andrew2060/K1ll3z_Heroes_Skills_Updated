package dev.k1ll3z.Heroes.skills;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.EffectType;
import com.herocraftonline.heroes.characters.effects.ExpirableEffect;
import com.herocraftonline.heroes.characters.skill.ActiveSkill;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;
import com.herocraftonline.heroes.characters.skill.SkillType;
import com.herocraftonline.heroes.util.Messaging;
import com.herocraftonline.heroes.util.Setting;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public class SkillLightningArrow extends ActiveSkill
{
  public SkillLightningArrow(Heroes plugin)
  {
    super(plugin, "LightningArrow");
    setDescription("Your arrows will strike lightning upon your target");
    setUsage("/skill larrow");
    setArgumentRange(0, 0);
    setIdentifiers(new String[] { "skill lightningarrow", "skill larrow" });
    setTypes(new SkillType[] { SkillType.LIGHTNING, SkillType.BUFF });
    Bukkit.getServer().getPluginManager().registerEvents(new SkillEntityListener(this), plugin);
  }

  public ConfigurationSection getDefaultConfig()
  {
    ConfigurationSection node = super.getDefaultConfig();
    node.set("mana-per-shot", Integer.valueOf(1));
    node.set(Setting.DAMAGE.node(), Integer.valueOf(5));
    return node;
  }

  public SkillResult use(Hero hero, String[] args)
  {
    Player player = hero.getPlayer();
    if (hero.hasEffect("LightningArrowBuff")) {
      hero.removeEffect(hero.getEffect("LightningArrowBuff"));
      return SkillResult.SKIP_POST_USAGE;
    }
    int duration = SkillConfigManager.getUseSetting(hero, this, Setting.DURATION, 30000, false);
    if (player.getItemInHand().getType() != Material.BOW) {
      Messaging.send(player, "You must be holding a bow to do this.", new Object[0]);
      return SkillResult.INVALID_TARGET_NO_MSG;
    }

    if (player.getItemInHand().getType() == Material.BOW)
      hero.addEffect(new LightningArrowBuff(this, duration));
    broadcastExecuteText(hero);
    return SkillResult.NORMAL;
  }

  public String getDescription(Hero hero)
  {
    int mana = SkillConfigManager.getUseSetting(hero, this, "mana-per-shot", 1, false);
    return getDescription().replace("$1", mana);
  }

  public class LightningArrowBuff extends ExpirableEffect
  {
    public LightningArrowBuff(Skill skill, long duration)
    {
      super("LightningArrowBuff", duration);
      this.types.add(EffectType.LIGHTNING);
      SkillLightningArrow.this.setDescription("lightning");
    }
  }

  public class SkillEntityListener implements Listener
  {
    private final Skill skill;

    public SkillEntityListener(Skill skill) {
      this.skill = skill;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
      if ((event.isCancelled()) || (!(event instanceof EntityDamageByEntityEvent)) || (!(event.getEntity() instanceof LivingEntity))) {
        return;
      }

      Entity projectile = ((EntityDamageByEntityEvent)event).getDamager();
      if ((!(projectile instanceof Arrow)) || (!(((Projectile)projectile).getShooter() instanceof Player))) {
        return;
      }

      Player player = (Player)((Projectile)projectile).getShooter();
      Hero hero = SkillLightningArrow.this.plugin.getCharacterManager().getHero(player);
      if (!hero.hasEffect("LightningArrowBuff")) {
        return;
      }

      event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityShootBow(EntityShootBowEvent event)
    {
      if ((event.isCancelled()) || (!(event.getEntity() instanceof Player)) || (!(event.getProjectile() instanceof Arrow))) {
        return;
      }
      Hero hero = SkillLightningArrow.this.plugin.getCharacterManager().getHero((Player)event.getEntity());
      if (hero.hasEffect("LightningArrowBuff")) {
        int mana = SkillConfigManager.getUseSetting(hero, this.skill, "mana-per-shot", 1, true);
        if (hero.getMana() < mana)
          hero.removeEffect(hero.getEffect("LightningArrowBuff"));
        else
          hero.setMana(hero.getMana() - mana);
      }
    }
  }
}

/* Location:           C:\Users\Andrew\Desktop\K1ll3z\bin\decomp.jar
 * Qualified Name:     dev.k1ll3z.Heroes.skills.SkillLightningArrow
 * JD-Core Version:    0.6.2
 */